package com.bl.todo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bl.todo.data.room.entities.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUserData(user : UserEntity) : Long

    @Query("select * from user_table where uid = :uid ")
    suspend fun getUserData(uid : Long) : UserEntity

}