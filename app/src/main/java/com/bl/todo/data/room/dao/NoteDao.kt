package com.bl.todo.data.room.dao

import androidx.room.*
import com.bl.todo.data.room.entities.NoteEntity

@Dao
interface NoteDao {

    @Insert
    suspend fun addNewNote(note : NoteEntity) : Long

    @Query("select * from notes_table")
    suspend fun getUserNotes() : List<NoteEntity>

    @Update
    suspend fun updateUserNotes(note: NoteEntity)

    @Delete
    suspend fun deleteUserNotes(note: NoteEntity)

//    @Query("update notes_table set fNoteId = :fnid where id = :id")
//    suspend fun updateFirebaseId(fnid : String, id: Long) : NoteEntity




}