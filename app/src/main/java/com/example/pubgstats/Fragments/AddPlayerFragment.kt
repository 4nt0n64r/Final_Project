package com.example.pubgstats.Fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pubgstats.API.getPlayer
import com.example.pubgstats.MainActivity
import com.example.pubgstats.R
import com.example.pubgstats.database.players.ErrorsData
import com.example.pubgstats.database.players.PlayerData
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.add_player_frag_layout.*


class AddPlayerFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_player_frag_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("FRAG", "Fragment AddPlayer started")

        btn_find.setOnClickListener {

            if (!input_login.text!!.isEmpty()) {

                getPlayer(input_login.text.toString()) { data ->
                    if (data is PlayerData){
                        log(data.getName())
                        log(data.getId())
                        val mainActivity = this.activity as MainActivity
                        mainActivity.addPlayer(data.getName(), data.getId(), true)
                    }else if (data is ErrorsData){
                        showSnackbar(data.error[0].title)
                        log(data.error[0].title)
                        log(data.error[0].detail)
                    }else{
                        showSnackbar("странные дела")
                    }
                }
            }else{
                val snack = Snackbar.make(parent,R.string.enter_player_name,Snackbar.LENGTH_SHORT)
                val sv = snack.view
                val tv = sv.findViewById<TextView>(R.id.snackbar_text)
                tv.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
                snack.show()
            }
        }
    }

    fun showSnackbar(msg: String) {
        val snack = Snackbar.make(parent, msg, Snackbar.LENGTH_LONG)
        val sv = snack.view
        val tv = sv.findViewById<TextView>(R.id.snackbar_text)
        tv.setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        snack.show()
    }
}


