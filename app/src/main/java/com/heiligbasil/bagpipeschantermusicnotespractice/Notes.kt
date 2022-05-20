package com.heiligbasil.bagpipeschantermusicnotespractice

enum class Notes(val note: String, val flipped: Boolean) {
    LOW_G(note = "(low) G", flipped = false),
    LOW_A(note = "(low) A", flipped = false),
    B(note = "B", flipped = true),
    C(note = "C", flipped = true),
    D(note = "D", flipped = true),
    E(note = "E", flipped = true),
    F(note = "F", flipped = true),
    HIGH_G(note = "(high) G", flipped = true),
    HIGH_A(note = "(high) A", flipped = true),
}
