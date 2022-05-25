package com.heiligbasil.bagpipeschantermusicnotespractice

enum class Notes(val notation: String, val visual: String, val flipped: Boolean) {
    LOW_G(notation = "G", visual = "(low) G", flipped = false),
    LOW_A(notation = "A", visual = "(low) A", flipped = false),
    B(notation = "B", visual = "B", flipped = true),
    C(notation = "C", visual = "C", flipped = true),
    D(notation = "D", visual = "D", flipped = true),
    E(notation = "E", visual = "E", flipped = true),
    F(notation = "F", visual = "F", flipped = true),
    HIGH_G(notation = "Ḡ", visual = "(high) G", flipped = true),
    HIGH_A(notation = "Ā", visual = "(high) A", flipped = true),
}
