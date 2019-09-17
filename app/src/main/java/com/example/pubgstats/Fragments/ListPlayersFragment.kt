package com.example.pubgstats.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.pubgstats.MainActivity
import com.example.pubgstats.MainActivity.Companion.ADD_PLAYER
import com.example.pubgstats.MainActivity.Companion.ID
import com.example.pubgstats.MainActivity.Companion.NAME
import com.example.pubgstats.MainActivity.Companion.PLAYER_ADDER
import com.example.pubgstats.R
import com.example.pubgstats.RecyclerItemClickListener
import com.example.pubgstats.adapters.PlayersAdapter
import com.example.pubgstats.database.DataStorage
import com.example.pubgstats.database.players.SimplePlayer
import com.example.pubgstats.database.players.SimplePlayerUI
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.listplayerfragment_layout.*
import kotlinx.android.synthetic.main.name_player_item_layout.view.*
import kotlinx.coroutines.*


class ListPlayersFragment : Fragment() {
    var players = mutableListOf<SimplePlayer>()
    var playersUI = mutableListOf<SimplePlayerUI>()

    private var name = ""
    private var id = ""

    lateinit var db: DataStorage
    lateinit var adapter: PlayersAdapter

    private val mainJob = SupervisorJob()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.listplayerfragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainJob.start()

        createDatabase()
        initAdapter()

        //добавили ли мы игрока?
        val bundle = this.arguments
        if (bundle != null) {
            name = bundle.getString(NAME)!!
            id = bundle.getString(ID)!!
            if (bundle.getBoolean(ADD_PLAYER)) {
                cachePlayerAndUpdateList(name, id)
            }
        } else {
            getPlayersFromDatabase()
        }

        my_recycler_view.addOnItemTouchListener(
            RecyclerItemClickListener(
                this@ListPlayersFragment.activity!!,
                my_recycler_view,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {

                        val mainActivity = this@ListPlayersFragment.activity as MainActivity

                        val touchedPlayer = CoroutineScope(Dispatchers.Main + mainJob).async {
                            withContext(Dispatchers.IO) {
                                db.daoFunctions().getPlayerByName(view!!.item_name.text.toString())
                            }
                        }

                        CoroutineScope(Dispatchers.Main + mainJob).launch {
                            mainActivity.showStatistics(
                                touchedPlayer.await().name,
                                touchedPlayer.await().id
                            )
                        }

                    }

                    @SuppressLint("RestrictedApi")//че он ругается?
                    override fun onLongItemClick(view: View?, position: Int) {

                        playersUI[position].isSelected = !playersUI[position].isSelected
                        adapter.notifyDataSetChanged()

                        if (playersUI.find { it.isSelected == true } != null) {
                            fab_delPlayer.visibility = VISIBLE
                        } else {
                            fab_delPlayer.visibility = INVISIBLE
                        }
                    }
                })
        )

        fab_addPlayer.setOnClickListener {
            val mainActivity = this@ListPlayersFragment.activity as MainActivity
            mainActivity.setFragment(PLAYER_ADDER)
        }

        fab_delPlayer.setOnClickListener {

            deletePlayersAndUpdateList(playersUI.filter { it.isSelected == true } as MutableList<SimplePlayerUI>)

            adapter.notifyDataSetChanged()

            fab_delPlayer.visibility = INVISIBLE
        }
    }

    override fun onDestroyView() {
        mainJob.cancel()
        super.onDestroyView()
    }

    private fun initAdapter() {
        my_recycler_view.layoutManager = LinearLayoutManager(activity!!.applicationContext)

        playersUI.clear()
        playersUI = arrToSPUI(players)

        adapter = PlayersAdapter(playersUI)
        my_recycler_view.adapter = adapter
    }

    private fun createDatabase() {
        db = Room.databaseBuilder(context!!, DataStorage::class.java, "playersDB").build()
        db.createDatabase(context!!)
    }

    private fun getPlayersFromDatabase() = CoroutineScope(Dispatchers.Main + mainJob).launch {
        val playersFromDb = withContext(Dispatchers.IO) { db.daoFunctions().getPlayersFromDB() }
        updateListOfPlayers(playersFromDb)
    }

    private fun deletePlayersAndUpdateList(players: MutableList<SimplePlayerUI>) =
        CoroutineScope(Dispatchers.Main + mainJob).launch {
            withContext(Dispatchers.IO) {
                for (player in players) {
                    deletePlayerFromDB(
                        SimplePlayer(
                            player.name,
                            player.id
                        )
                    )
                }
            }

            players.clear()

            val playersFromDb =
                withContext(Dispatchers.IO) { db.daoFunctions().getPlayersFromDB() }

            updateListOfPlayers(playersFromDb)
        }

    //выполняет кеширование игрока в базу и обновление списка на экране
    private fun cachePlayerAndUpdateList(name: String, id: String) =
        CoroutineScope(Dispatchers.Main + mainJob).launch {
            withContext(Dispatchers.IO) {
                writePlayerToDB(
                    SimplePlayer(
                        name,
                        id
                    )
                )
            }

            players.clear()

            val playersFromDb =
                withContext(Dispatchers.IO) { db.daoFunctions().getPlayersFromDB() }

            updateListOfPlayers(playersFromDb)
        }

    private fun deletePlayerFromDB(player: SimplePlayer) {
        db.daoFunctions().deletePlayer(player.name, player.id)
        Log.d("DB", "deleted $player from DB")
    }

    //записывает игрока в базу и в лог
    private fun writePlayerToDB(player: SimplePlayer) {
        db.daoFunctions().addPlayerToDB(player)
        Log.d("DB", "Add $player to DB")
    }

    //принимает список игроков из базы, обновляет список в RV и показывает заглушку если список пуст
    fun updateListOfPlayers(playersFromDb: List<SimplePlayer>) {

        playersUI.clear()
        playersUI = arrToSPUI(playersFromDb as MutableList<SimplePlayer>)

        adapter.setData(playersUI)

        if (playersFromDb.isEmpty()) {
            layout_error_list.visibility = VISIBLE
        } else {
            layout_error_list.visibility = INVISIBLE
        }
    }

    fun showSnackbar(msg: String) {
        val snack = Snackbar.make(constr, msg, Snackbar.LENGTH_LONG)
        val sv = snack.view
        val tv = sv.findViewById<TextView>(R.id.snackbar_text)
        tv.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        snack.show()
    }

    fun arrToSPUI(players: MutableList<SimplePlayer>): MutableList<SimplePlayerUI> {
        var arr = mutableListOf<SimplePlayerUI>()
        for (player in players) {
            arr.add(player.toSPUI())
        }
        return arr
    }

    fun arrToSP(players: MutableList<SimplePlayerUI>): MutableList<SimplePlayer> {
        var arr = mutableListOf<SimplePlayer>()
        for (player in players) {
            arr.add(player.toSP())
        }
        return arr
    }
}

fun log(msg: String) = println(msg)


