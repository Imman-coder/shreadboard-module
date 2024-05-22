package com.immanlv.shredboard.util

import com.immanlv.shredboard.data.NoteCarrier

abstract class RowArrangement{
    abstract val spacing : List<Int>

    fun getRowFirstNote(rowNumber: Int): NoteCarrier {
        return NoteCarrier(spacing[rowNumber])
    }
}