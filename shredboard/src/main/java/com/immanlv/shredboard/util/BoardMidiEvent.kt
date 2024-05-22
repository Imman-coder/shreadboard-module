package com.immanlv.shredboard.util



sealed class BoardMidiEvent {
    data class NoteOnEvent(val note: Int, val velocity: Int, val channel: Int, val touchId: Long) :
        BoardMidiEvent()

    data class NoteOffEvent(val note: Int, val velocity: Int, val channel: Int, val touchId: Long) :
        BoardMidiEvent()

    data class PitchBendEvent(val lsb: Int, val msb: Int, val channel: Int, val touchId: Long) :
        BoardMidiEvent()

    data class YEvent(val value: Int, val channel: Int, val touchId: Long) : BoardMidiEvent()
}