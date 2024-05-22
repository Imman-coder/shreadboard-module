package com.immanlv.shredboard.util

import com.immanlv.shredboard.data.NoteCarrier

abstract class ScaleMode {
    abstract val notes: List<Int>
    abstract val localName: String

    fun getNotesWithRoot(root: Int): List<NoteCarrier> {
        val l = mutableListOf<NoteCarrier>()

        notes.forEach { l.add(NoteCarrier(root + it)) }

        return l.toList()
    }

    fun includes(root: Int, note: NoteCarrier): Boolean {
        val rNote: Int = root
        val nNote: Int = note.truncatedIdentifier
        notes.forEach {
            if ((rNote + it) % 12 == nNote) return true
        }
        return false
    }

}