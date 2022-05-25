package com.heiligbasil.bagpipeschantermusicnotespractice

enum class SessionType(val displayText: String) {
    RANDOM("Play random notes"),
    RANDOM_NO_DUPES("Play random notes without consecutive repetition"),
    SCALE_UP("Play up the scale continuously"),
    SCALE_DOWN("Play down the scale continuously"),
    SCALE_BOTH("Play up and down the scale in order"),
    STAGGERED_UP("Play up each note to every other note in order"),
    STAGGERED_DOWN("Play down each note to every other note in order"),
    STAGGERED_BOTH("Play up and down each note to every other note in order"),
    PRESET_1("Play this preset: Exercise 4 (Beginning the Bagpipe)"),
    PRESET_2("Play this preset: Exercise 5 (Beginning the Bagpipe)"),
    PRESET_3("Play this preset: Exercise 6 (Beginning the Bagpipe)"),
    PRESET_4("Play this preset: Exercise 7 (Beginning the Bagpipe)"),
    PRESET_5("Play this preset: Exercise 8 (Beginning the Bagpipe)"),
    PRESET_6("Play this preset: Exercise 9 (Beginning the Bagpipe)"),
}
