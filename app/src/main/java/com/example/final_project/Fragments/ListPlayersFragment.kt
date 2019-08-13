package com.example.final_project.Fragments

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
import com.example.final_project.MainActivity
import com.example.final_project.MainActivity.Companion.ADD_PLAYER
import com.example.final_project.MainActivity.Companion.ID
import com.example.final_project.MainActivity.Companion.NAME
import com.example.final_project.MainActivity.Companion.PLAYER_ADDER
import com.example.final_project.MainActivity.Companion.STATISTICS
import com.example.final_project.PlayersAdapter
import com.example.final_project.R
import com.example.final_project.RecyclerItemClickListener
import com.example.final_project.database.PlayersDB
import com.example.final_project.database.players.SimplePlayer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.listplayerfragment_layout.*
import kotlinx.android.synthetic.main.name_player_item_layout.view.*
import kotlinx.coroutines.*


class ListPlayersFragment : Fragment() {
    var players = mutableListOf<SimplePlayer>()

    private var name = ""
    private var id = ""

    lateinit var db: PlayersDB
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

        log("$players")

        my_recycler_view.addOnItemTouchListener(
            RecyclerItemClickListener(
                this@ListPlayersFragment.activity!!,
                my_recycler_view,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val mainActivity = this@ListPlayersFragment.activity as MainActivity
                        //получить игрока из базы по нику
                        //передать его данные через бандл
                        val touchedPlayer = CoroutineScope(Dispatchers.Main + mainJob).async{
                            withContext(Dispatchers.IO) {db.simplePlayerDAO().getPlayerByName(view!!.item_name.text.toString())}
                        }
                        CoroutineScope(Dispatchers.Main+mainJob).launch {
                            log(touchedPlayer.await().toString())
                            mainActivity.showStatistics(touchedPlayer.await().name,touchedPlayer.await().id)
                        }
                    }

                    override fun onLongItemClick(view: View?, position: Int) {
                        view!!.item_background.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.colorAccent
                            )
                        )
                        view!!.item_name.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
                        fab_delPlayer.visibility = VISIBLE
                        showSnackbar("Длинное нажатие отработало нормально")
                    }
                })
        )

        fab_addPlayer.setOnClickListener {
            val mainActivity = this@ListPlayersFragment.activity as MainActivity
            mainActivity.setFragment(PLAYER_ADDER)
        }

        fab_delPlayer.setOnClickListener {
            showSnackbar("Удаление игрока пока не реализовано!")
        }
    }

    override fun onDestroyView() {
        mainJob.cancel()
        super.onDestroyView()
    }

    private fun initAdapter() {
        my_recycler_view.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        adapter = PlayersAdapter(players)
        my_recycler_view.adapter = adapter
    }

    private fun createDatabase() {
        db = Room.databaseBuilder(context!!, PlayersDB::class.java, "playersDB").build()
        db.createDatabase(context!!)
    }

    private fun getPlayersFromDatabase() = CoroutineScope(Dispatchers.Main + mainJob).launch {
        val playersFromDb = withContext(Dispatchers.IO) { db.simplePlayerDAO().getPlayersFromDB() }
        log("$playersFromDb")
        updateListOfPlayers(playersFromDb)
    }

    private fun deletePlayerAndUpdateList(name: String, id: String) =
        CoroutineScope(Dispatchers.Main + mainJob).launch {
            withContext(Dispatchers.IO) { deletePlayerFromDB(SimplePlayer(name, id)) }

            players.clear()

            val playersFromDb =
                withContext(Dispatchers.IO) { db.simplePlayerDAO().getPlayersFromDB() }

            updateListOfPlayers(playersFromDb)
        }

    //выполняет кеширование игрока в базу и обновление списка на экране
    private fun cachePlayerAndUpdateList(name: String, id: String) = CoroutineScope(Dispatchers.Main + mainJob).launch {
        withContext(Dispatchers.IO) { writePlayerToDB(SimplePlayer(name, id)) }

        players.clear()

        val playersFromDb =
            withContext(Dispatchers.IO) { db.simplePlayerDAO().getPlayersFromDB() }

        updateListOfPlayers(playersFromDb)
    }

    private fun deletePlayerFromDB(player: SimplePlayer) {
        db.simplePlayerDAO().deletePlayer(player.name, player.id)
        Log.d("DB", "deleted $player from DB")
    }


    //записывает игрока в базу и в лог
    private fun writePlayerToDB(player: SimplePlayer) {
        db.simplePlayerDAO().addPlayerToDB(player)
        Log.d("DB", "Add $player to DB")
    }

    //принимает список игроков из базы, обновляет список в RV и показывает заглушку если список пуст
    fun updateListOfPlayers(playersFromDb: List<SimplePlayer>) {
        players.addAll(playersFromDb)
        adapter.setData(players)

        if (players.isEmpty()) {
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
}

fun log(msg: String) = println(msg)


