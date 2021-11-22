package com.bl.todo.ui.wrapper

import java.util.*

data class NoteInfo(
    var title: String, var content: String, var fnid: String = "",
    var nid: Long = 0L, var dateModified: Date?, var archived : Boolean = false,
    var reminder : Date? = null
)
