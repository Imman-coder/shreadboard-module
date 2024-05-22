package com.immanlv.shredboard.util




sealed class ScaleNotation(open val scaleNotations:List<String>){
    data class NameChangesWithScaleExclusive(override val scaleNotations: List<String>):ScaleNotation(scaleNotations)
    data class NameChangesWithScale(override val scaleNotations: List<String>):ScaleNotation(scaleNotations)
    data class NameIsConstant(override val scaleNotations: List<String>):ScaleNotation(scaleNotations)


}

