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

    PRESET_1("Right-handed exercise 1", "GABGABBAGBAG"),
    PRESET_2("Right-handed exercise 2", "BCBCBCBCBCBC"),
    PRESET_3("Right-handed exercise 3", "GABCBAGGABCBAG"),
    PRESET_4("Right-handed exercise 4", "CDCDCBACDCDACD"),
    PRESET_5("Right-handed exercise 5", "GABCDCBAGABCDCD"),
    PRESET_6("Right-handed exercise 6", "ADCDBGAADBDACD"),

    PRESET_7("Left-handed exercise 1", "ḠĀḠĀḠĀḠĀḠĀ"),
    PRESET_8("Left-handed exercise 2", "EĀFĀEĀFĀEFĀ"),
    PRESET_9("Left-handed exercise 3", "EFḠEFḠEFḠEFḠĀ"),
    PRESET_10("Left-handed exercise 4", "EFḠEFḠĀFEĀFEEḠĀ"),
    PRESET_11("Left-handed exercise 5", "EĀĀFEĀĀFEĀĀFḠĀ"),
    PRESET_12("Left-handed exercise 6", "ĀḠFEFḠEFḠĀFḠEĀ"),

    PRESET_13("Both-hands exercise 1", "DEDEDEDEDEDE"),
    PRESET_14("Both-hands exercise 2", "AĀBĀCĀDĀEĀFĀḠĀĀ"),
    PRESET_15("Both-hands exercise 3", "DFĀFDFĀFĀFĀACD"),
    PRESET_16("Both-hands exercise 4", "ADFĀADFADFĀḠED"),
    PRESET_17("Both-hands exercise 5", "CEĀECEĀDFĀFDFĀ"),
    PRESET_18("Both-hands exercise 6", "ĀFDFĀFDFḠFEĀḠED"),

    PRESET_19("Gracenotes 1", "GǥA_AǥB_BǥC_CǥB_BǥA_AǥG"),
    PRESET_20("Gracenotes 2", "GđA_AđB_BđC_CđB_BđA_AđG"),
    PRESET_21("Gracenotes 3", "GɇA_AɇB_BɇC_CɇB_BɇA_AɇG"),
    PRESET_22("Gracenotes 4", "ǥGǥAǥBǥCǥDǥEǥFǥEǥDǥCǥBǥAǥG"),
    PRESET_23("Gracenotes 5", "đGđAđBđCđDđEđFđEđDđCđBđAđG"),
    PRESET_24("Gracenotes 6", "ɇGɇAɇBɇCɇDɇEɇFɇEɇDɇCɇBɇAɇG"),
    PRESET_25("Gracenotes 7", "ǥGđGɇGǥAđAɇAǥBđBɇBǥCđCɇCǥCđCɇCǥBđBɇBǥAđAɇAǥGđGɇG"),

    PRESET_26("Hot Cross Buns 1", "CBACBAǥAđAɇ[strike]AǥBđBɇB[strike]BCBA"),
}
