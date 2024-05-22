package com.immanlv.shredboard

import com.immanlv.shredboard.util.ScaleNotation

val swaraNotation =
    ScaleNotation.NameChangesWithScaleExclusive(listOf("Sa", "Re", "Ga", "Ma", "Pa", "Dha", "Ni"))

val arezzoNotation =
    ScaleNotation.NameChangesWithScaleExclusive(listOf("Do", "Re", "Mi", "Fa", "Sol", "La", "Si"))

val flatNotation =
    ScaleNotation.NameIsConstant(listOf("C","Db","D","Eb","E","F","Gb","G","Ab","A","Bb","B"))

val sharpNotation =
    ScaleNotation.NameIsConstant(listOf("C","C#","D","D#","E","F","F#","G","G#","A","A#","B"))