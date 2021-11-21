package com.bl.todo.data.room.dao

import androidx.room.*
import com.bl.todo.data.room.entities.NoteEntity

@Dao
interface NoteDao {

    @Insert
    suspend fun addNewNote(note: NoteEntity): Long

    @Query("select * from notes_table where archived = 0")
    suspend fun getUserNotes(): List<NoteEntity>

    @Update
    suspend fun updateUserNotes(note: NoteEntity)

    @Delete
    suspend fun deleteUserNotes(note: NoteEntity)

    @Query("delete from notes_table")
    suspend fun clearNoteTable()

    @Query("select * from notes_table where archived = 1")
    suspend fun getArchivedNotes() : List<NoteEntity>


}