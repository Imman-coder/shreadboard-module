package com.immanlv.shredboard.data

import com.immanlv.shredboard.FifthArrangement
import com.immanlv.shredboard.ScaleModeMajor
import com.immanlv.shredboard.flatNotation
import com.immanlv.shredboard.util.ScaleNotation
import com.immanlv.shredboard.util.RowArrangement
import com.immanlv.shredboard.util.ScaleMode

data class ShredboardConfig(
    val numRows: Int = -1,
    val numColumns: Int = -1,
    val lowestNote: Int = 7,
    val rowArrangement: RowArrangement = FifthArrangement(),
    val renderHints: Boolean = false,
    val hintFollowPitch: Boolean = true,
    val globalTranspose: Int = 2,
    val rootNote: Int = 0,
    val scaleMode: ScaleMode = ScaleModeMajor,
    val notation: ScaleNotation = flatNotation,
    val pitchBendRange: Int = 12,
    val pitchStickyness: Float = 4f
) {
    fun isScaleNote(note: NoteCarrier) = scaleMode.includes(rootNote,note)
    fun isRootNote(note: NoteCarrier) = note.truncatedIdentifier == rootNote
    fun getNotation(note: NoteCarrier):String{
        when(notation){
            is ScaleNotation.NameIsConstant ->{
                return notation.scaleNotations[note.truncatedIdentifier]
            }

            is ScaleNotation.NameChangesWithScale -> {
                val nn = (note.noteNumber - rootNote)%12
                return notation.scaleNotations[nn]
            }

            is ScaleNotation.NameChangesWithScaleExclusive -> {
                if(isScaleNote(note)){
                    val nn = (note + rootNote).truncatedIdentifier
                    val k = scaleMode.notes.indexOf(nn)
                    return notation.scaleNotations[k]
                } else {
                    return ""
                }
            }
        }
    }
}

