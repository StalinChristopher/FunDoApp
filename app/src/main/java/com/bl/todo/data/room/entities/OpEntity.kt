package com.bl.todo.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "operation_table")
data class OpEntity(
    @PrimaryKey
    val fNid: String,
    val opCode: Int,
)