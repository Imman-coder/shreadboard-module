package com.immanlv.shredboard.data


class NoteCarrier{
    var noteNumber:Int = 24
        private set

    val truncatedIdentifier:Int
        get() = noteNumber%12

    constructor()

    constructor(v:Int) {
        noteNumber = v
    }



    operator fun plus(v:Int): NoteCarrier {
        return NoteCarrier(this.noteNumber + v)
    }

    operator fun plus(v: NoteCarrier): NoteCarrier {
        return NoteCarrier(this.noteNumber + v.noteNumber)
    }

    operator fun minus(v:Int): NoteCarrier {
        return NoteCarrier(this.noteNumber - v)
    }

    operator fun minus(v: NoteCarrier): NoteCarrier {
        return NoteCarrier(this.noteNumber - v.noteNumber)
    }

    fun octaveShift(shift:Int): NoteCarrier {
        return NoteCarrier(this.noteNumber + (12*shift))
    }

    fun transpose(step:Int): NoteCarrier {
        return NoteCarrier(this.noteNumber + (step))
    }

    fun equalsIgnoreTranspose(note: NoteCarrier):Boolean{
        return (this.truncatedIdentifier == note.truncatedIdentifier)
    }

}

