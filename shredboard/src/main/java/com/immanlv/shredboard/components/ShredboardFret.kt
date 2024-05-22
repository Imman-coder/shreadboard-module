package com.immanlv.shredboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.immanlv.shredboard.ShredboardColors
import com.immanlv.shredboard.ShredboardShapes

@Composable
fun ShredboardFret(
    widthPercentage: Float,
    highlight: Boolean,
    rootNote: Boolean,
    noteName: String,
    colors: ShredboardColors,
    shapes: ShredboardShapes
) {

    Box(
        modifier = Modifier
            .fillMaxWidth(widthPercentage)
            .fillMaxHeight()
            .padding(1 .dp)
            .clip(shapes.boardShape())
            .background(
                if (rootNote) colors.rootFretBackgroundColor()
                else colors.fretBackgroundColor(enabled = highlight)
            )
            .border(
                BorderStroke(
                    shapes.borderWidth(),
                    if (rootNote) colors.rootFretBorderColor() else colors.fretBorderColor(enabled = highlight)
                ),
                shape = shapes.boardShape()
            ), contentAlignment = Alignment.Center


    ) {
        Text(
            text = noteName,
            color = if (rootNote) colors.rootFretTextColor()
            else colors.fretTextColor(enabled = highlight)
        )
    }
}
