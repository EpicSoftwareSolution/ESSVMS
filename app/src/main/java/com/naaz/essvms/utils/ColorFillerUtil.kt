package com.naaz.essvms.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import com.naaz.essvms.R

class ColorFillerUtil {
     fun getErrorColor(context: Context): ColorStateList {
        val color: Int = Color.rgb(255, 0, 0)
        val color2 = Color.parseColor(context.resources.getString(R.string.error_color_string))
        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(android.R.attr.state_hovered),
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf()
        )
        val colors = intArrayOf(
            color,
            color,
            color,
            color2
        )
        return ColorStateList(states, colors)
    }
}