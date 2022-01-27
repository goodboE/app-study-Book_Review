package com.example.app_study_book_review

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.app_study_book_review.Model.History
import com.example.app_study_book_review.Model.Review
import com.example.app_study_book_review.dao.HistoryDao
import com.example.app_study_book_review.dao.ReviewDao

@Database(entities = [History::class, Review::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun ReviewDao(): ReviewDao
}