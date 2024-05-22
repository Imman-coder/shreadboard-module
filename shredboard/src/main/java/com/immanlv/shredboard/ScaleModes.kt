package com.immanlv.shredboard

import com.immanlv.shredboard.util.ScaleMode

object ScaleModeMajor: ScaleMode() {
    override val notes: List<Int>
        get() = listOf(0,2,4,5,7,9,11)
    override val localName: String
        get() = "Major"
}

object ScaleModeMinor: ScaleMode() {
    override val notes: List<Int>
        get() = listOf(0,2,3,5,7,8,10)
    override val localName: String
        get() = "Minor/Aolean"
}