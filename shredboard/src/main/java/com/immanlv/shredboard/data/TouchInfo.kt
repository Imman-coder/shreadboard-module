package com.immanlv.shredboard.data

import androidx.compose.ui.geometry.Offset


data class TouchInfo(
    val initialPosition: Offset,
    val maxX: Int = -1,
    val maxY: Int = -1,
    val minX: Int = -1,
    val minY: Int = -1,
    val initialX: Int = -1,
    val initialY: Int = -1,
    var isBend: Boolean = false,
    var currentPosition: Offset = Offset.Zero,
    var pitchPosition: Offset = Offset.Zero,
    var note: NoteCarrier
)

fun List<TouchInfo>.contains(note: NoteCarrier): Boolean {
    this.forEach {
        if (it.note == note) return true
    }
    return false
}