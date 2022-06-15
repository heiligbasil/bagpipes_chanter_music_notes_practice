package com.heiligbasil.bagpipeschantermusicnotespractice

import com.heiligbasil.bagpipeschantermusicnotespractice.Constants.CHAR_BAR_END
import com.heiligbasil.bagpipeschantermusicnotespractice.Constants.CHAR_GRACENOTE

enum class Symbols(val notation: String, val visual: String) {
    END_BAR(notation = "|", visual = CHAR_BAR_END),
    PAUSE(notation = "_", visual = ""),
    GRACE_NOTE_G(notation = "ǥ", visual = CHAR_GRACENOTE),
    GRACE_NOTE_D(notation = "đ", visual = CHAR_GRACENOTE),
    GRACE_NOTE_E(notation = "ɇ", visual = CHAR_GRACENOTE),
}
