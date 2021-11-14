package com.bl.todo.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val uid : Long = 0L,
    @ColumnInfo(name = "fid") val fid : String?,
    @ColumnInfo(name = "userName") val userName : String,
    @ColumnInfo(name = "email") val email : String,
    @ColumnInfo(name = "phone") val phone : String,
)
