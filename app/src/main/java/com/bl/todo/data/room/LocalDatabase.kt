package com.bl.todo.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bl.todo.data.room.dao.NoteDao
import com.bl.todo.data.room.dao.UserDao
import com.bl.todo.data.room.entities.NoteEntity
import com.bl.todo.data.room.entities.UserEntity

@Database(entities = [UserEntity::class,NoteEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateTypeConverters::class)
abstract class LocalDatabase : RoomDatabase(){

    abstract fun userDao() : UserDao
    abstract fun noteDao() : NoteDao

    companion object{
        @Volatile
        private var INSTANCE : LocalDatabase? = null

        fun getInstance(context : Context) : LocalDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "funDo_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}