package com.example.final_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        var name = mutableListOf<String>()
//        name.add("id1234567890")
//        name.add("id1234567890")
//        name.add("id1234567890")
//        name.add("id1234567890")
//        name.add("id1234567890")
//        name.add("id1234567890")

//        my_recycler_view.layoutManager = LinearLayoutManager(this)
//        my_recycler_view.adapter = Adapter(name)


        supportActionBar?.hide()

        btn_find.setOnClickListener {
            intent = Intent(this,Main22Activity::class.java)
            startActivity(intent)
        }

    }

//    class Adapter(private val name: List<String>) : RecyclerView.Adapter<Adapter.ViewHolder>() {
//        lateinit var itemView: View
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            itemView = LayoutInflater.from(parent.context).inflate(R.layout.id_player_item_layout, parent, false)
//            return ViewHolder(itemView)
//
//        }
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.name?.text = name[position]
//        }
//
//        override fun getItemCount() = name.size
//        class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
//            var name: TextView? = null
//
//            init {
//                name = itemView?.findViewById(R.id.name)
//            }
//        }
//    }
}
