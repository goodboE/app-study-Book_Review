package com.example.app_study_book_review

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.app_study_book_review.Model.History
import com.example.app_study_book_review.dao.HistoryDao

@Database(entities = [History::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}