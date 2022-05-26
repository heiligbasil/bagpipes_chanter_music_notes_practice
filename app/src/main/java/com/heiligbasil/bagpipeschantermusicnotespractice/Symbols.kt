package com.heiligbasil.bagpipeschantermusicnotespractice

import com.heiligbasil.bagpipeschantermusicnotespractice.Constants.CHAR_BAR_END

enum class Symbols(val notation: String, val visual: String) {
    END_BAR(notation = "|", visual = CHAR_BAR_END),
    PAUSE(notation = "_", visual = ""),
}
