package com.immanlv.shredboard

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.immanlv.shredboard.components.ShredboardFret
import com.immanlv.shredboard.data.NoteCarrier
import com.immanlv.shredboard.data.ShredboardConfig
import com.immanlv.shredboard.data.TouchInfo
import com.immanlv.shredboard.util.BoardMidiEvent
import com.immanlv.shredboard.util.clipToRange
import java.lang.Exception
import kotlin.math.abs

@Composable
fun Shredboard(
    config: ShredboardConfig,
    shapes: ShredboardShapes = ShredboardDefaults.shape(),
    colors: ShredboardColors = ShredboardDefaults.flatShredboardColors(),
    onMidiEvent: (BoardMidiEvent) -> Unit
) {
    var winSize by remember { mutableStateOf(IntSize.Zero) }

    val touches: SnapshotStateMap<Long, TouchInfo> = remember {
        mutableStateMapOf()
    }

    fun NoteCarrier.notation(): String {
        return config.getNotation(this)
    }

    fun getBlockSize(): IntSize {
        return IntSize(
            winSize.width / config.numColumns, winSize.height / config.numRows
        )
    }

    fun getBlockMid(pos: Offset): Offset {
        val blockSize = getBlockSize()
        val xId = (pos.x / blockSize.width).toInt()
        val yId = (pos.y / blockSize.height).toInt()
        return Offset(
            ((blockSize.width / 2) + xId * blockSize.width).toFloat(),
            ((blockSize.height / 2) + yId * blockSize.height).toFloat()
        )
    }

    fun sendPitchBend(it: PointerInputChange) {
        try {
            val ix = touches[it.id.value]!!.initialPosition.x
            val cx = touches[it.id.value]!!.pitchPosition.x
            val sw = getBlockSize().width.toFloat() * config.pitchBendRange
            val move = clipToRange(ix - sw, ix + sw, cx) - ix
            val moveP = move / sw
            val moveF = ((moveP + 1) / 2 * 16383).toInt()
            val lsb = moveF and 0xF7
            val msb = (moveF shr 7 and 0xFF)
            onMidiEvent(BoardMidiEvent.PitchBendEvent(lsb, msb, 1, it.id.value))
//        println("msb:${msb} lsb:${lsb}")
        } catch (e: Exception) {
            Log.e("TAG", "sendPitchBend: $e")
        }
    }

    fun calculatePitchNormalization(it: PointerInputChange): Offset {
        val timeDiff = it.uptimeMillis - it.previousUptimeMillis
        val disDiff = it.position.x - it.previousPosition.x
        var vel = abs(disDiff / timeDiff)
        val mid = getBlockMid(it.position)
        if (vel.isNaN()) vel = 0f
        vel *= config.pitchStickyness
        if (vel > 1f) vel = 1f
        val prevMid = getBlockMid(it.previousPosition)
        return if (mid.x != prevMid.x) Offset(
            x = (mid.x + prevMid.x) / 2, y = it.position.y
        )
        else Offset(
            x = mid.x - ((mid.x - it.position.x) * vel), y = it.position.y
        )

    }

    fun getNote(xId: Int, yId: Int): NoteCarrier {

        val rowFirstNote = config.rowArrangement.getRowFirstNote(yId)
        val lowestNote = config.lowestNote
        val rootNote = config.rootNote
        val transpose = config.globalTranspose

        return rowFirstNote + lowestNote + rootNote + xId + (12 * transpose)
    }

    fun getNote(pos: Offset): NoteCarrier {
        val blockSize = getBlockSize()
        val xId = (pos.x / blockSize.width).toInt()
        val yId = config.numRows - (pos.y / blockSize.height).toInt() - 1
        return getNote(xId, yId)
    }

    fun onTouch(it: PointerInputChange) {

        // if touch is already computed then skip
        if (touches[it.id.value] != null) return

        // Calculate Note under Touch
        val note = getNote(it.position)

        val midPosition = getBlockMid(it.position).copy(y = it.position.y)


        // Add touch info to the touch list
        touches[it.id.value] = TouchInfo(
            initialPosition = midPosition,
            pitchPosition = midPosition,
            currentPosition = it.position,
            note = note
        )

        println("Note On: ${note.notation()}")

        onMidiEvent(
            BoardMidiEvent.NoteOnEvent(
                note.noteNumber, 127, 1, it.id.value
            )
        )

        onMidiEvent(
            BoardMidiEvent.PitchBendEvent(
                63, 63, 1, it.id.value
            )
        )
    }

    fun onTouchMove(it: PointerInputChange) {
        // Handle Touch outside the Playboard
        if (it.position.x < 0 || it.position.y < 0) {
            val note = touches[it.id.value]?.note

            // If Note not handled earlier
            if (note != null) {
                println("Note Off: ${note.notation()}")
                touches.remove(it.id.value)
                onMidiEvent(
                    BoardMidiEvent.NoteOffEvent(
                        note.noteNumber, 127, 1, it.id.value
                    )
                )
            }

        } else {
            if (touches.contains(it.id.value)) {
                // Update Touch info
                val p = touches[it.id.value]
                p!!.currentPosition = it.position
                p.pitchPosition = calculatePitchNormalization(it)

                // Re add event to recompose the position hint
                touches.remove(it.id.value)
                touches[it.id.value] = p

                sendPitchBend(it)


            } else {
                // Calculate Note under Touch
                val note = getNote(it.position)

                // Add touch info to the touch list
                touches[it.id.value] = TouchInfo(
                    initialPosition = getBlockMid(it.position),
                    currentPosition = it.position,
                    note = note
                )

                println("Note On: ${note.notation()}")

                // Send Midi Note On Message
                onMidiEvent(
                    BoardMidiEvent.NoteOnEvent(
                        note.noteNumber, 127, 1, it.id.value
                    )
                )
            }

        }
    }

    fun onTouchEnd(it: PointerInputChange) {
        val note = touches[it.id.value]?.note
        if (!it.pressed && note != null) {
            println("Note Off: ${note.notation()}")
            onMidiEvent(
                BoardMidiEvent.NoteOffEvent(
                    note.noteNumber, 127, 1, it.id.value
                )
            )
            touches.remove(it.id.value)
        }
    }

    Box(
        modifier = Modifier.background(colors.backgroundColor())
    ) {
        Column(Modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    do {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Press -> event.changes.forEach { onTouch(it) }

                            PointerEventType.Move -> event.changes.forEach { onTouchMove(it) }

                            PointerEventType.Release -> event.changes.forEach { onTouchEnd(it) }
                        }

                    } while (event.changes.any { it.pressed })
                }
            }
            .onGloballyPositioned {
                winSize = it.size
            }
            .align(Alignment.BottomEnd)

        ) {

            for (row in config.numRows - 1 downTo 0) {

                val heightPercentage = 1 / (row.toFloat() + 1)

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(heightPercentage)) {
                    for (col in 0 until config.numColumns) {
                        val note = getNote(col, row)
                        ShredboardFret(
                            widthPercentage = 1 / (config.numColumns.toFloat() - col),
                            highlight = config.isScaleNote(note),
                            rootNote = config.isRootNote(note),
                            noteName = note.notation(),
                            colors = colors,
                            shapes = shapes
                        )
                    }
                }
            }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (config.renderHints) {
                touches.toList().forEach {

                        drawCircle(
                            Color.White,
                            100f,
                            if (config.hintFollowPitch) it.second.pitchPosition else it.second.currentPosition,
                            style = Stroke(width = 5f)
                        )
                    }
            }
        }
    }
}

object ShredboardDefaults {


    fun shape(
        boardShape: Shape = RoundedCornerShape(12.dp),
        boardStroke: Dp = 4.dp
    ): ShredboardShapes = ShredboardShapes(
        boardShape = boardShape,
        borderWidth = boardStroke
    )

    @Composable
    fun materialShape(
        boardShape: Shape,
        borderWidth: Dp
    ): ShredboardShapes = ShredboardShapes(
        boardShape = boardShape,
        borderWidth = borderWidth

    )

    fun flatShredboardColors(
        backgroundColor: Color = Color.Black,
        fretBorderColor: Color = Color(0xFF0CCBF9),
        fretBackgroundColor: Color = Color(0x800CCBF9),
        disabledFretBorderColor: Color = Color.Gray,
        disabledFretBackgroundColor: Color = Color.Gray.copy(alpha = 0.3f),
        rootFretBackgroundColor: Color = Color(0x80FFBF25),
        fretTextColor: Color = Color.White,
        disabledFretTextColor: Color = Color.White,
        rootFretTextColor: Color = Color.White,
        rootFretBorderColor: Color = Color(0xFFFFBF25)
    ) : ShredboardColors = ShredboardColors(
        backgroundColor = backgroundColor,
        fretBorderColor = fretBorderColor,
        fretBackgroundColor = fretBackgroundColor,
        disabledFretBorderColor = disabledFretBorderColor,
        disabledFretBackgroundColor = disabledFretBackgroundColor,
        rootFretBackgroundColor = rootFretBackgroundColor,
        fretTextColor = fretTextColor,
        disabledFretTextColor = disabledFretTextColor,
        rootFretTextColor = rootFretTextColor,
        rootFretBorderColor = rootFretBorderColor
    )

    @Composable
    fun materialShredboardColors(
        backgroundColor: Color = MaterialTheme.colorScheme.surface,
        fretBorderColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
        fretBackgroundColor: Color = MaterialTheme.colorScheme.secondary,
        disabledFretBorderColor: Color = MaterialTheme.colorScheme.outline,
        disabledFretBackgroundColor: Color = backgroundColor,
        rootFretBackgroundColor: Color = MaterialTheme.colorScheme.primary,
        fretTextColor: Color = MaterialTheme.colorScheme.onSecondary,
        disabledFretTextColor: Color = MaterialTheme.colorScheme.onSurface,
        rootFretTextColor: Color = MaterialTheme.colorScheme.onPrimary,
        rootFretBorderColor: Color = MaterialTheme.colorScheme.outline
    ) : ShredboardColors = ShredboardColors(
        backgroundColor = backgroundColor,
        fretBorderColor = fretBorderColor,
        fretBackgroundColor = fretBackgroundColor,
        disabledFretBorderColor = disabledFretBorderColor,
        disabledFretBackgroundColor = disabledFretBackgroundColor,
        rootFretBackgroundColor = rootFretBackgroundColor,
        fretTextColor = fretTextColor,
        disabledFretTextColor = disabledFretTextColor,
        rootFretTextColor = rootFretTextColor,
        rootFretBorderColor = rootFretBorderColor
    )
}

@Immutable
class ShredboardShapes internal constructor(
    private val boardShape: Shape,
    private val borderWidth: Dp
){
    fun boardShape():Shape = boardShape
    fun borderWidth():Dp = borderWidth
}

@Immutable
class ShredboardColors internal constructor(
    private val backgroundColor: Color,
    private val fretBorderColor: Color,
    private val fretBackgroundColor: Color,
    private val fretTextColor: Color,
    private val disabledFretBorderColor: Color,
    private val disabledFretTextColor: Color,
    private val rootFretBorderColor: Color,
    private val disabledFretBackgroundColor: Color,
    private val rootFretBackgroundColor: Color,
    private val rootFretTextColor: Color,
) {

    @Composable
    internal fun backgroundColor():Color{
        return backgroundColor
    }

    @Composable
    internal fun rootFretBackgroundColor():Color{
        return rootFretBackgroundColor
    }

    @Composable
    internal fun rootFretTextColor():Color{
        return rootFretTextColor
    }

    @Composable
    internal fun rootFretBorderColor():Color{
        return rootFretBorderColor
    }

    @Composable
    internal fun fretBorderColor(enabled: Boolean):Color{
        return if(enabled) fretBorderColor else disabledFretBorderColor
    }

    @Composable
    internal fun fretTextColor(enabled: Boolean):Color{
        return if(enabled) fretTextColor else disabledFretTextColor
    }

    @Composable
    internal fun fretBackgroundColor(enabled:Boolean):Color{
        return if(enabled) fretBackgroundColor else disabledFretBackgroundColor
    }
}



