package com.example.final_project.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.final_project.*
import com.example.final_project.API.getSeasonStats
import com.example.final_project.API.getSeasons
import com.example.final_project.MainActivity.Companion.ID
import com.example.final_project.MainActivity.Companion.LIST_OF_PLAYERS
import com.example.final_project.MainActivity.Companion.NAME
import com.example.final_project.adapters.MySpinnerAdapter
import com.example.final_project.adapters.StatisticsAdapter
import com.example.final_project.database.DataStorage
import com.example.final_project.database.seasons.DownloadDate
import com.example.final_project.database.seasons.SeasonDB
import com.example.final_project.database.seasons.SeasonUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.statfragment_layout.*
import kotlinx.coroutines.*
import java.util.*


class StatFragment : Fragment() {

    var seasonsTmp = mutableListOf<String>()
    var seasons = mutableListOf<String>()
    var seasonsListUI = mutableListOf<SeasonUI>()

    var playerId: String = ""
    var nameOfPlayer: String = ""

    lateinit var db: DataStorage
    lateinit var spAdapter: MySpinnerAdapter
    lateinit var rvAdapter: StatisticsAdapter

    private val mainJob = SupervisorJob()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.statfragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainJob.start()

        createDatabase()
        initSpinnerAdapter()
        initRecyclerAdapter()

        val bundle = this.arguments
        if (bundle != null) {
            nameOfPlayer = bundle.getString(NAME)!!
            playerId = bundle.getString(ID)!!
            txt_playerName.text = nameOfPlayer
        }

        btn_exitStat.setOnClickListener {
            val mainActivity = this@StatFragment.activity as MainActivity
            mainActivity.setFragment(LIST_OF_PLAYERS)
        }

        //получение сезонов
        seasonsRequest()

        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    //TODO(идея: получить всю data (скорее всего она и приходит вся) и закешировать с записью времени, потом убрать загрузку и отобразить solo как и выделено в начале)
                    R.id.navigation_solo -> {
                        Log.d("show_solo", spinner.selectedItem.toString())
                        getSeasonStats(
                            playerId,
                            seasonsListUI[spinner.selectedItemPosition].seasonId
                        ) { data ->
                            Log.d(
                                "stat_solo",
                                data.data.seasonAttributes.gameModeStats.solo.assists.toString()
                            )

                            val statisticsData = listOf<StatList>(
                                StatHeader(getString(R.string.stat_txt_rank)),
                                StatPoints(
                                    getString(R.string.rankPoints),
                                    data.data.seasonAttributes.gameModeStats.solo.rankPoints.toString()
                                ),
                                StatPoints(
                                    getString(R.string.bestRankPoint),
                                    data.data.seasonAttributes.gameModeStats.solo.bestRankPoint.toString()
                                ),
                                StatPoints(
                                    getString(R.string.roundsPlayed),
                                    data.data.seasonAttributes.gameModeStats.solo.roundsPlayed.toString()
                                ),
                                StatPoints(
                                    getString(R.string.assists),
                                    data.data.seasonAttributes.gameModeStats.solo.assists.toString()
                                ),
                                StatPoints(
                                    getString(R.string.top10s),
                                    data.data.seasonAttributes.gameModeStats.solo.top10s.toString()
                                ),
                                StatPoints(
                                    getString(R.string.mostSurvivalTime),
                                    data.data.seasonAttributes.gameModeStats.solo.mostSurvivalTime.toString()
                                ),
                                StatPoints(
                                    getString(R.string.swimDistance),
                                    data.data.seasonAttributes.gameModeStats.solo.swimDistance.toString()
                                ),

                                StatHeader(getString(R.string.stat_txt_kills)),
                                StatPoints(
                                    getString(R.string.kills),
                                    data.data.seasonAttributes.gameModeStats.solo.kills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.longestKill),
                                    data.data.seasonAttributes.gameModeStats.solo.longestKill.toString()
                                ),
                                StatPoints(
                                    getString(R.string.roundMostKills),
                                    data.data.seasonAttributes.gameModeStats.solo.roundMostKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.headshotKills),
                                    data.data.seasonAttributes.gameModeStats.solo.headshotKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.dailyKills),
                                    data.data.seasonAttributes.gameModeStats.solo.dailyKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.weeklyKills),
                                    data.data.seasonAttributes.gameModeStats.solo.weeklyKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.maxKillStreaks),
                                    data.data.seasonAttributes.gameModeStats.solo.maxKillStreaks.toString()
                                ),
                                StatPoints(
                                    getString(R.string.roadKills),
                                    data.data.seasonAttributes.gameModeStats.solo.roadKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.teamKills),
                                    data.data.seasonAttributes.gameModeStats.solo.teamKills.toString()
                                ),

                                StatHeader(getString(R.string.stat_txt_other)),
                                StatPoints(
                                    getString(R.string.dBNOs),
                                    data.data.seasonAttributes.gameModeStats.solo.dBNOs.toString()
                                ),
                                StatPoints(
                                    getString(R.string.dailyWins),
                                    data.data.seasonAttributes.gameModeStats.solo.dailyWins.toString()
                                ),
                                StatPoints(
                                    getString(R.string.boosts),
                                    data.data.seasonAttributes.gameModeStats.solo.boosts.toString()
                                ),
                                StatPoints(
                                    getString(R.string.damageDealt),
                                    data.data.seasonAttributes.gameModeStats.solo.damageDealt.toString()
                                ),
                                StatPoints(
                                    getString(R.string.days),
                                    data.data.seasonAttributes.gameModeStats.solo.days.toString()
                                ),
                                StatPoints(
                                    getString(R.string.heals),
                                    data.data.seasonAttributes.gameModeStats.solo.heals.toString()
                                ),
                                StatPoints(
                                    getString(R.string.losses),
                                    data.data.seasonAttributes.gameModeStats.solo.losses.toString()
                                ),
                                StatPoints(
                                    getString(R.string.revives),
                                    data.data.seasonAttributes.gameModeStats.solo.revives.toString()
                                ),
                                StatPoints(
                                    getString(R.string.rideDistance),
                                    data.data.seasonAttributes.gameModeStats.solo.rideDistance.toString()
                                ),
                                StatPoints(
                                    getString(R.string.suicides),
                                    data.data.seasonAttributes.gameModeStats.solo.suicides.toString()
                                ),
                                StatPoints(
                                    getString(R.string.vehicleDestroys),
                                    data.data.seasonAttributes.gameModeStats.solo.vehicleDestroys.toString()
                                ),
                                StatPoints(
                                    getString(R.string.weaponsAcquired),
                                    data.data.seasonAttributes.gameModeStats.solo.weaponsAcquired.toString()
                                )
                            )
                            rvAdapter.setData(statisticsData)
                        }
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_duo -> {
                        Log.d("show_duo", spinner.selectedItem.toString())
                        getSeasonStats(
                            playerId,
                            seasonsListUI[spinner.selectedItemPosition].seasonId
                        ) { data ->
                            Log.d(
                                "stat_duo",
                                data.data.seasonAttributes.gameModeStats.duo.assists.toString()
                            )

                            val statisticsData = listOf<StatList>(
                                StatHeader(getString(R.string.stat_txt_rank)),
                                StatPoints(
                                    getString(R.string.rankPoints),
                                    data.data.seasonAttributes.gameModeStats.duo.rankPoints.toString()
                                ),
                                StatPoints(
                                    getString(R.string.bestRankPoint),
                                    data.data.seasonAttributes.gameModeStats.duo.bestRankPoint.toString()
                                ),
                                StatPoints(
                                    getString(R.string.roundsPlayed),
                                    data.data.seasonAttributes.gameModeStats.duo.roundsPlayed.toString()
                                ),
                                StatPoints(
                                    getString(R.string.assists),
                                    data.data.seasonAttributes.gameModeStats.duo.assists.toString()
                                ),
                                StatPoints(
                                    getString(R.string.top10s),
                                    data.data.seasonAttributes.gameModeStats.duo.top10s.toString()
                                ),
                                StatPoints(
                                    getString(R.string.mostSurvivalTime),
                                    data.data.seasonAttributes.gameModeStats.duo.mostSurvivalTime.toString()
                                ),
                                StatPoints(
                                    getString(R.string.swimDistance),
                                    data.data.seasonAttributes.gameModeStats.duo.swimDistance.toString()
                                ),

                                StatHeader(getString(R.string.stat_txt_kills)),
                                StatPoints(
                                    getString(R.string.kills),
                                    data.data.seasonAttributes.gameModeStats.duo.kills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.longestKill),
                                    data.data.seasonAttributes.gameModeStats.duo.longestKill.toString()
                                ),
                                StatPoints(
                                    getString(R.string.roundMostKills),
                                    data.data.seasonAttributes.gameModeStats.duo.roundMostKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.headshotKills),
                                    data.data.seasonAttributes.gameModeStats.duo.headshotKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.dailyKills),
                                    data.data.seasonAttributes.gameModeStats.duo.dailyKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.weeklyKills),
                                    data.data.seasonAttributes.gameModeStats.duo.weeklyKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.maxKillStreaks),
                                    data.data.seasonAttributes.gameModeStats.duo.maxKillStreaks.toString()
                                ),
                                StatPoints(
                                    getString(R.string.roadKills),
                                    data.data.seasonAttributes.gameModeStats.duo.roadKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.teamKills),
                                    data.data.seasonAttributes.gameModeStats.duo.teamKills.toString()
                                ),

                                StatHeader(getString(R.string.stat_txt_other)),
                                StatPoints(
                                    getString(R.string.dBNOs),
                                    data.data.seasonAttributes.gameModeStats.duo.dBNOs.toString()
                                ),
                                StatPoints(
                                    getString(R.string.dailyWins),
                                    data.data.seasonAttributes.gameModeStats.duo.dailyWins.toString()
                                ),
                                StatPoints(
                                    getString(R.string.boosts),
                                    data.data.seasonAttributes.gameModeStats.duo.boosts.toString()
                                ),
                                StatPoints(
                                    getString(R.string.damageDealt),
                                    data.data.seasonAttributes.gameModeStats.duo.damageDealt.toString()
                                ),
                                StatPoints(
                                    getString(R.string.days),
                                    data.data.seasonAttributes.gameModeStats.duo.days.toString()
                                ),
                                StatPoints(
                                    getString(R.string.heals),
                                    data.data.seasonAttributes.gameModeStats.duo.heals.toString()
                                ),
                                StatPoints(
                                    getString(R.string.losses),
                                    data.data.seasonAttributes.gameModeStats.duo.losses.toString()
                                ),
                                StatPoints(
                                    getString(R.string.revives),
                                    data.data.seasonAttributes.gameModeStats.duo.revives.toString()
                                ),
                                StatPoints(
                                    getString(R.string.rideDistance),
                                    data.data.seasonAttributes.gameModeStats.duo.rideDistance.toString()
                                ),
                                StatPoints(
                                    getString(R.string.suicides),
                                    data.data.seasonAttributes.gameModeStats.duo.suicides.toString()
                                ),
                                StatPoints(
                                    getString(R.string.vehicleDestroys),
                                    data.data.seasonAttributes.gameModeStats.duo.vehicleDestroys.toString()
                                ),
                                StatPoints(
                                    getString(R.string.weaponsAcquired),
                                    data.data.seasonAttributes.gameModeStats.duo.weaponsAcquired.toString()
                                )
                            )
                            rvAdapter.setData(statisticsData)
                        }
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_squad -> {
                        Log.d("show_squad", spinner.selectedItem.toString())
                        getSeasonStats(
                            playerId,
                            seasonsListUI[spinner.selectedItemPosition].seasonId
                        ) { data ->
                            Log.d(
                                "stat_squad",
                                data.data.seasonAttributes.gameModeStats.squad.assists.toString()
                            )

                            val statisticsData = listOf<StatList>(
                                StatHeader(getString(R.string.stat_txt_rank)),
                                StatPoints(
                                    getString(R.string.rankPoints),
                                    data.data.seasonAttributes.gameModeStats.squad.rankPoints.toString()
                                ),
                                StatPoints(
                                    getString(R.string.bestRankPoint),
                                    data.data.seasonAttributes.gameModeStats.squad.bestRankPoint.toString()
                                ),
                                StatPoints(
                                    getString(R.string.roundsPlayed),
                                    data.data.seasonAttributes.gameModeStats.squad.roundsPlayed.toString()
                                ),
                                StatPoints(
                                    getString(R.string.assists),
                                    data.data.seasonAttributes.gameModeStats.squad.assists.toString()
                                ),
                                StatPoints(
                                    getString(R.string.top10s),
                                    data.data.seasonAttributes.gameModeStats.squad.top10s.toString()
                                ),
                                StatPoints(
                                    getString(R.string.mostSurvivalTime),
                                    data.data.seasonAttributes.gameModeStats.squad.mostSurvivalTime.toString()
                                ),
                                StatPoints(
                                    getString(R.string.swimDistance),
                                    data.data.seasonAttributes.gameModeStats.squad.swimDistance.toString()
                                ),

                                StatHeader(getString(R.string.stat_txt_kills)),
                                StatPoints(
                                    getString(R.string.kills),
                                    data.data.seasonAttributes.gameModeStats.squad.kills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.longestKill),
                                    data.data.seasonAttributes.gameModeStats.squad.longestKill.toString()
                                ),
                                StatPoints(
                                    getString(R.string.roundMostKills),
                                    data.data.seasonAttributes.gameModeStats.squad.roundMostKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.headshotKills),
                                    data.data.seasonAttributes.gameModeStats.squad.headshotKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.dailyKills),
                                    data.data.seasonAttributes.gameModeStats.squad.dailyKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.weeklyKills),
                                    data.data.seasonAttributes.gameModeStats.squad.weeklyKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.maxKillStreaks),
                                    data.data.seasonAttributes.gameModeStats.squad.maxKillStreaks.toString()
                                ),
                                StatPoints(
                                    getString(R.string.roadKills),
                                    data.data.seasonAttributes.gameModeStats.squad.roadKills.toString()
                                ),
                                StatPoints(
                                    getString(R.string.teamKills),
                                    data.data.seasonAttributes.gameModeStats.squad.teamKills.toString()
                                ),

                                StatHeader(getString(R.string.stat_txt_other)),
                                StatPoints(
                                    getString(R.string.dBNOs),
                                    data.data.seasonAttributes.gameModeStats.squad.dBNOs.toString()
                                ),
                                StatPoints(
                                    getString(R.string.dailyWins),
                                    data.data.seasonAttributes.gameModeStats.squad.dailyWins.toString()
                                ),
                                StatPoints(
                                    getString(R.string.boosts),
                                    data.data.seasonAttributes.gameModeStats.squad.boosts.toString()
                                ),
                                StatPoints(
                                    getString(R.string.damageDealt),
                                    data.data.seasonAttributes.gameModeStats.squad.damageDealt.toString()
                                ),
                                StatPoints(
                                    getString(R.string.days),
                                    data.data.seasonAttributes.gameModeStats.squad.days.toString()
                                ),
                                StatPoints(
                                    getString(R.string.heals),
                                    data.data.seasonAttributes.gameModeStats.squad.heals.toString()
                                ),
                                StatPoints(
                                    getString(R.string.losses),
                                    data.data.seasonAttributes.gameModeStats.squad.losses.toString()
                                ),
                                StatPoints(
                                    getString(R.string.revives),
                                    data.data.seasonAttributes.gameModeStats.squad.revives.toString()
                                ),
                                StatPoints(
                                    getString(R.string.rideDistance),
                                    data.data.seasonAttributes.gameModeStats.squad.rideDistance.toString()
                                ),
                                StatPoints(
                                    getString(R.string.suicides),
                                    data.data.seasonAttributes.gameModeStats.squad.suicides.toString()
                                ),
                                StatPoints(
                                    getString(R.string.vehicleDestroys),
                                    data.data.seasonAttributes.gameModeStats.squad.vehicleDestroys.toString()
                                ),
                                StatPoints(
                                    getString(R.string.weaponsAcquired),
                                    data.data.seasonAttributes.gameModeStats.squad.weaponsAcquired.toString()
                                )
                            )
                            rvAdapter.setData(statisticsData)
                        }
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun cacheSeasons() = CoroutineScope(Dispatchers.Main + mainJob).launch {
        saveSeasonsDownloadDate()

        withContext(Dispatchers.IO) {
            db.daoFunctions().addSeasonToDB(SeasonDB(seasonsTmp[seasonsTmp.size - 2]))
            db.daoFunctions().addSeasonToDB(SeasonDB(seasonsTmp[seasonsTmp.size - 1]))
        }
    }

    private fun seasonsRequest() = CoroutineScope(Dispatchers.Main + mainJob).launch {
        val shouldDownloadSeasons = checkDate()

        if (shouldDownloadSeasons.await()) {
            getSeasons("pc-ru") { data ->
                for (item in data.seasons) {
                    seasonsTmp.add(item.id)
                }

                seasons.add(seasonsTmp[seasonsTmp.size - 2])
                seasons.add(seasonsTmp[seasonsTmp.size - 1])

                seasonsListUI.clear()

                seasonsListUI.add(
                    SeasonUI(
                        seasons[0],
                        getString(R.string.prev_season)
                    )
                )
                seasonsListUI.add(
                    SeasonUI(
                        seasons[1],
                        getString(R.string.current_season)
                    )
                )

                spAdapter.updateView()

                cacheSeasons()
            }
        } else {
            getSeasonsFromDB()
        }
    }

    private fun checkDate() = CoroutineScope(Dispatchers.Main + mainJob).async {
        val dateFromDB = CoroutineScope(Dispatchers.Main + mainJob).async {
            withContext(Dispatchers.IO) { db.daoFunctions().getDate() }
        }

        val seasonsExistence = withContext(Dispatchers.IO) { db.daoFunctions().getSeasons() }

        if ((dateFromDB.await() != null) and (!seasonsExistence.isNullOrEmpty())) {
            return@async (dateFromDB.await().ddate != Calendar.getInstance().getTime().date)
        } else {
            return@async true
        }
    }

    private fun getSeasonsFromDB() = CoroutineScope(Dispatchers.Main + mainJob).launch {
        val seasonsFromDB = withContext(Dispatchers.IO) {
            db.daoFunctions().getSeasons()
        }

        if (!seasonsFromDB.isNullOrEmpty()) {
            seasonsListUI.clear()

            seasonsListUI.add(
                SeasonUI(
                    seasonsFromDB[0].seasonId,
                    getString(R.string.prev_season)
                )
            )
            seasonsListUI.add(
                SeasonUI(
                    seasonsFromDB[1].seasonId,
                    getString(R.string.current_season)
                )
            )

            spAdapter.updateView()
            log("Сезоны получены из базы!")
        } else {

        }
    }

    private fun saveSeasonsDownloadDate() = CoroutineScope(Dispatchers.Main + mainJob).launch {
        withContext(Dispatchers.IO) {
            db.daoFunctions().deleteDate()
            db.daoFunctions().addDateToDB(DownloadDate(Calendar.getInstance().getTime().date))
        }
    }

    fun showSnackbar(msg: String) {
        val snack = Snackbar.make(frame_layout, msg, Snackbar.LENGTH_LONG)
        val sv = snack.view
        val tv = sv.findViewById<TextView>(R.id.snackbar_text)
        tv.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        snack.show()
    }

    private fun initSpinnerAdapter() {
        seasonsListUI.add(
            SeasonUI(
                "0",
                getString(R.string.loading)
            )
        )

        spAdapter = MySpinnerAdapter(context!!, seasonsListUI)
        spinner.adapter = spAdapter

        spAdapter.updateView()
    }

    private fun initRecyclerAdapter() {
        stat_data_rv.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        stat_data_rv.adapter = rvAdapter
        val defaultData = listOf(
            StatHeader(getString(R.string.stat_txt_rank)),
            StatPoints(
                getString(R.string.rankPoints),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.bestRankPoint),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.roundsPlayed),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.assists),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.top10s),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.mostSurvivalTime),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.swimDistance),
                getString(R.string.default_value)
            ),

            StatHeader(getString(R.string.stat_txt_kills)),
            StatPoints(
                getString(R.string.kills),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.longestKill),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.roundMostKills),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.headshotKills),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.dailyKills),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.weeklyKills),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.maxKillStreaks),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.roadKills),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.teamKills),
                getString(R.string.default_value)
            ),

            StatHeader(getString(R.string.stat_txt_other)),
            StatPoints(
                getString(R.string.dBNOs),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.dailyWins),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.boosts),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.damageDealt),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.days),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.heals),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.losses),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.revives),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.rideDistance),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.suicides),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.vehicleDestroys),
                getString(R.string.default_value)
            ),
            StatPoints(
                getString(R.string.weaponsAcquired),
                getString(R.string.default_value)
            )
        )
        rvAdapter.setData(defaultData)
    }

    private fun createDatabase() {
        db = Room.databaseBuilder(context!!, DataStorage::class.java, "seasonsDB").build()
        db.createDatabase(context!!)
    }


}







