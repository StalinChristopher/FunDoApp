package com.bl.todo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bl.todo.data.room.entities.OpEntity

@Dao
interface OpDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOp(opEntity : OpEntity)

    @Query("select * from operation_table where fNid = :fNid")
    suspend fun getOpCode(fNid : String) : OpEntity

    @Query("delete from operation_table")
    suspend fun deleteAllOps()
}