package com.example.pubgstats.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pubgstats.database.players.SimplePlayer
import com.example.pubgstats.database.seasons.DownloadDate
import com.example.pubgstats.database.seasons.SeasonDB

@Dao
interface DAOFunctions {

    //players

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlayerToDB(player: SimplePlayer)

    @Query("SELECT * FROM players_table")
    fun getPlayersFromDB():List<SimplePlayer>

    @Query("DELETE FROM players_table WHERE name_field = :name AND id_field = :id")
    fun deletePlayer(name: String, id:String)

    @Query("SELECT * FROM players_table WHERE name_field = :name")
    fun getPlayerByName(name: String): SimplePlayer

    //seasons

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSeasonToDB(season: SeasonDB)

    @Query("DELETE FROM seasons")
    fun deleteSeasons()

    @Query("SELECT * FROM seasons")
    fun getSeasons():List<SeasonDB>

    //date

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDateToDB(date: DownloadDate)

    @Query("SELECT * from download" )
    fun getDate(): DownloadDate

    @Query("DELETE FROM download" )
    fun deleteDate()
}

