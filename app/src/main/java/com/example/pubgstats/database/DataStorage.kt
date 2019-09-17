package com.example.pubgstats.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pubgstats.database.players.SimplePlayer
import com.example.pubgstats.database.seasons.DownloadDate
import com.example.pubgstats.database.seasons.SeasonDB


@Database(
    entities = [SimplePlayer::class, SeasonDB::class, DownloadDate::class],
    version = 1
)
abstract class DataStorage : RoomDatabase() {

    abstract fun daoFunctions(): DAOFunctions

    fun createDatabase(context: Context) {
        Room.databaseBuilder(context.applicationContext, DataStorage::class.java, "storage.db")
            .build()
//        Room.databaseBuilder(context.applicationContext, DataStorage::class.java, "seasons.db")
//            .build()
//        Room.databaseBuilder(context.applicationContext, DataStorage::class.java, "download.db")
//            .build()
    }
}
