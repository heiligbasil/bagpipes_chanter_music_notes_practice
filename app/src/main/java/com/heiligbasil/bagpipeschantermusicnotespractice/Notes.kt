package com.heiligbasil.bagpipeschantermusicnotespractice

import androidx.annotation.DrawableRes

enum class Notes(@DrawableRes val resource: Int) {
    LOW_G(R.drawable.low_g),
    LOW_A(R.drawable.low_a),
    B(R.drawable.b),
    C(R.drawable.c),
    D(R.drawable.d),
    E(R.drawable.e),
    F(R.drawable.f),
    HIGH_G(R.drawable.high_g),
    HIGH_A(R.drawable.high_a),
}