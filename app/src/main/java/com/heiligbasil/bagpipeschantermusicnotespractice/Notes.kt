package com.heiligbasil.bagpipeschantermusicnotespractice

enum class Notes(val notation: String, val visual: String, val flip: Boolean) {
    LOW_G(notation = "G", visual = "(low) G", flip = false),
    LOW_A(notation = "A", visual = "(low) A", flip = false),
    B(notation = "B", visual = "B", flip = true),
    C(notation = "C", visual = "C", flip = true),
    D(notation = "D", visual = "D", flip = true),
    E(notation = "E", visual = "E", flip = true),
    F(notation = "F", visual = "F", flip = true),
    HIGH_G(notation = "Ḡ", visual = "(high) G", flip = true),
    HIGH_A(notation = "Ā", visual = "(high) A", flip = true),
}
