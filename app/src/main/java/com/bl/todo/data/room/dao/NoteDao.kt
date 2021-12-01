package com.bl.todo.data.room.dao

import androidx.room.*
import com.bl.todo.data.room.entities.NoteEntity

@Dao
interface NoteDao {

    @Insert
    suspend fun addNewNote(note: NoteEntity): Long

    @Query("select * from notes_table where archived = 0 ORDER BY dateModified")
    suspend fun getUserNotes(): List<NoteEntity>

    @Query("select * from notes_table where archived = 0 ORDER BY dateModified DESC LIMIT :limit OFFSET :offset")
    suspend fun getPagedNotes(limit: Int, offset: Int): List<NoteEntity>

    @Query("select COUNT(*) from notes_table where archived = 0")
    suspend fun getNotesCount() : Int

    @Update
    suspend fun updateUserNotes(note: NoteEntity)

    @Delete
    suspend fun deleteUserNotes(note: NoteEntity)

    @Query("delete from notes_table")
    suspend fun clearNoteTable()

    @Query("select * from notes_table where archived = 1 ORDER BY dateModified DESC")
    suspend fun getArchivedNotes() : List<NoteEntity>

    @Query("select * from notes_table where archived = 1 ORDER BY dateModified DESC LIMIT :limit OFFSET :offset")
    suspend fun getArchivedPaged(limit: Int, offset: Int) : List<NoteEntity>

    @Query("select * from notes_table where NULLIF(reminder,'') is not NULL ORDER BY dateModified DESC")
    suspend fun getReminderNotes() : List<NoteEntity>

    @Query("select * from notes_table where NULLIF(reminder, '') is not NULL ORDER BY dateModified DESC LIMIT :limit OFFSET :offset")
    suspend fun getReminderPaged(limit: Int, offset: Int) : List<NoteEntity>

    @Query("select count(*) from notes_table where archived = 1")
    suspend fun getArchiveCount(): Int

    @Query("select count(*) from notes_table where NULLIF(reminder,'') IS NOT NULL")
    suspend fun getReminderCount(): Int


}