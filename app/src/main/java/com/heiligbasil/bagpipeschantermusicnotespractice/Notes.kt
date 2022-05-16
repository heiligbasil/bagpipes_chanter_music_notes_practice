package com.heiligbasil.bagpipeschantermusicnotespractice

import androidx.annotation.DrawableRes

enum class Notes(val note: String, @DrawableRes val resource: Int) {
    LOW_G("(low) G", R.drawable.low_g),
    LOW_A("(low) A", R.drawable.low_a),
    B("B", R.drawable.b),
    C("C", R.drawable.c),
    D("D", R.drawable.d),
    E("E", R.drawable.e),
    F("F", R.drawable.f),
    HIGH_G("(high) G", R.drawable.high_g),
    HIGH_A("(high) A", R.drawable.high_a),
}