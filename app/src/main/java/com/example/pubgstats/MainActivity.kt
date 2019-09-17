package com.example.pubgstats

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pubgstats.Fragments.AddPlayerFragment
import com.example.pubgstats.Fragments.ComparisonFragment
import com.example.pubgstats.Fragments.ListPlayersFragment
import com.example.pubgstats.Fragments.StatFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity_layout)
        val actionBar = supportActionBar
        actionBar!!.hide()

        setFragment(LIST_OF_PLAYERS)

    }


    //    нужно обработать ситуацию с нажатым + просто так
    fun addPlayer(name: String, id: String, doWeAddPlayer: Boolean) {
        if (doWeAddPlayer) {
            val currentFrag = ListPlayersFragment()
            if (!name.equals("") and !id.equals("")) {
                val bundle = Bundle()
                bundle.putString(NAME, name)
                bundle.putString(ID, id)
                bundle.putBoolean(ADD_PLAYER, doWeAddPlayer)
                currentFrag.arguments = bundle
            }
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment, currentFrag, FRAGMENT_CHANGED)
                .commit()
        } else
            setFragment(LIST_OF_PLAYERS)
    }

    fun showStatistics(name: String, id: String) {
        val currentFrag = StatFragment()
        if (!name.equals("") and !id.equals("")) {
            val bundle = Bundle()
            bundle.putString(NAME, name)
            bundle.putString(ID, id)
            currentFrag.arguments = bundle
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, currentFrag, FRAGMENT_CHANGED)
            .commit()

    }

    fun setFragment(fragmentId: Int) {
        when (fragmentId) {
            LIST_OF_PLAYERS -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment, ListPlayersFragment(), FRAGMENT_CHANGED)
                    .commit()
            }
            PLAYER_ADDER -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment, AddPlayerFragment(), FRAGMENT_CHANGED)
                    .commit()
            }
            STATISTICS -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment, StatFragment(), FRAGMENT_CHANGED)
                    .commit()
            }
            COMPARSION -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment, ComparisonFragment(), FRAGMENT_CHANGED)
                    .commit()
            }

        }
    }

    companion object {
        val LIST_OF_PLAYERS = 1
        val PLAYER_ADDER = 2
        val STATISTICS = 3
        val COMPARSION = 4

        val FRAGMENT_CHANGED = "fragment changed"
        val NAME = "NAME"
        val ID = "ID"
        val ADD_PLAYER = "ADD_PLAYER"
    }
}


