package com.example.demo_muzicapp.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demo_muzicapp.R
import com.example.demo_muzicapp.data.model.Song
import com.example.demo_muzicapp.databinding.ItemHistoryBinding
import com.example.demo_muzicapp.ui.activities.PlayerActivity

class HistoryAdapter(private val list: ArrayList<Song>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = list[position]
        val binding = holder.binding

        binding.txtTitle.text = song.title
        binding.txtArtist.text = song.artist

        val resId = holder.itemView.context.resources.getIdentifier(
            song.image,
            "drawable",
            holder.itemView.context.packageName
        )

        if (resId != 0) {
            binding.imgSong.setImageResource(resId)
        } else {
            binding.imgSong.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("file", song.file)

            val fileList = ArrayList(list.map { it.file })
            intent.putStringArrayListExtra("list", fileList)
            intent.putExtra("index", position)

            context.startActivity(intent)
        }
    }
}