package com.bl.todo.data.models

data class FirebaseNewNote(var title : String, var content : String,
                           var dateModified : String?, var archived : Boolean = false,
                           var reminder : String = "")
