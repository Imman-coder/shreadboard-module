package com.immanlv.shredboard

import com.immanlv.shredboard.util.RowArrangement

class FifthArrangement: RowArrangement(){
    override val spacing: List<Int>
        get() = listOf(0,5,10,15,20,25,30,35,40,45)
}

class GuitarArrangement: RowArrangement(){
    override val spacing: List<Int>
        get() = listOf(0,5,10,14,19,24,29,34,39,44)
}