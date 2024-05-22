package com.immanlv.shredboard.util

import kotlin.math.max
import kotlin.math.min


fun clipToRange(min: Int, max: Int, value: Int) = max(min, min(max, value))
fun clipToRange(min: Float, max: Float, value: Float) = max(min, min(max, value))
