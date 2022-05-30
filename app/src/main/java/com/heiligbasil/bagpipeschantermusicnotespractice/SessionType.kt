package com.heiligbasil.bagpipeschantermusicnotespractice

enum class SessionType(val displayText: String, val notes: String) {
    RANDOM("Play random notes", ""),
    RANDOM_NO_DUPES("Play random notes without consecutive repetition", ""),

    SCALE_UP("Play up the scale continuously", ""),
    SCALE_DOWN("Play down the scale continuously", ""),
    SCALE_BOTH("Play up and down the scale in order", ""),
    STAGGERED_UP("Play up each note to every other note in order", ""),
    STAGGERED_DOWN("Play down each note to every other note in order", ""),
    STAGGERED_BOTH("Play up and down each note to every other note in order", ""),

    PRESET_1("Right-handed exercise 1", "GABGABBAGBAG|_"),
    PRESET_2("Right-handed exercise 2", "BCBCBCBCBCBC|_"),
    PRESET_3("Right-handed exercise 3", "GABCBAGGABCBAG|_"),
    PRESET_4("Right-handed exercise 4", "CDCDCBACDCDACD|_"),
    PRESET_5("Right-handed exercise 5", "GABCDCBAGABCDCD|_"),
    PRESET_6("Right-handed exercise 6", "ADCDBGAADBDACD|_"),

    PRESET_7("Left-handed exercise 1", "ḠĀḠĀḠĀḠĀḠĀ|_"),
    PRESET_8("Left-handed exercise 2", "EĀFĀEĀFĀEFĀ|_"),
    PRESET_9("Left-handed exercise 3", "EFḠEFḠEFḠEFḠĀ|_"),
    PRESET_10("Left-handed exercise 4", "EFḠEFḠĀFEĀFEEḠĀ|_"),
    PRESET_11("Left-handed exercise 5", "EĀĀFEĀĀFEĀĀFḠĀ|_"),
    PRESET_12("Left-handed exercise 6", "ĀḠFEFḠEFḠĀFḠEĀ|_"),

    PRESET_13("Both-hands exercise 1", "DEDEDEDEDEDE|_"),
    PRESET_14("Both-hands exercise 2", "AĀBĀCĀDĀEĀFĀḠĀĀ|_"),
    PRESET_15("Both-hands exercise 3", "DFĀFDFĀFĀFĀACD|_"),
    PRESET_16("Both-hands exercise 4", "ADFĀADFADFĀḠED|_"),
    PRESET_17("Both-hands exercise 5", "CEĀECEĀDFĀFDFĀ|_"),
    PRESET_18("Both-hands exercise 6", "ĀFDFĀFDFḠFEĀḠED|_"),
}
