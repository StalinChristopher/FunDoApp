package com.bl.todo.ui.wrapper

import java.util.*

data class LabelDetails(
    var labelName: String,
    var labelFid: String = "",
    var dateModified: Date?,
    var isChecked: Boolean = false
)
