package com.example.final_project.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.final_project.database.players.SimplePlayer

@Dao
interface SimplePlayerDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPlayerToDB(player: SimplePlayer)

    @Query("SELECT * FROM simple_players")
    fun getPlayersFromDB():List<SimplePlayer>

    @Query("DELETE FROM simple_players WHERE name_field = :name AND id_field = :id")
    fun deletePlayer(name: String, id:String)

    @Query("SELECT * FROM simple_players WHERE name_field = :name")
    fun getPlayerByName(name: String):SimplePlayer
}


//
//    @Query("SELECT id,name_field,data_field FROM players")
//    fun selectAll(): List<Player>
//
//    @Query("SELECT id,name_field,data_field FROM players WHERE name_field = :name")
//    fun findByName(name: String): Player
