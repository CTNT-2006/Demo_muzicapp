package com.example.demo_muzicapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val list: ArrayList<Song>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.txtTitle)
        val artist: TextView = view.findViewById(R.id.txtArtist)
        val image: ImageView = view.findViewById(R.id.imgSong)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = list[position]

        holder.title.text = song.title
        holder.artist.text = song.artist

        val resId = holder.itemView.context.resources.getIdentifier(
            song.image,
            "drawable",
            holder.itemView.context.packageName
        )

        if (resId != 0) {
            holder.image.setImageResource(resId)
        } else {
            holder.image.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("song", song.file)

            context.startActivity(intent)
        }
    }
}