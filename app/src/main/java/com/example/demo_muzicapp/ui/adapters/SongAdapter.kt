package com.example.demo_muzicapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demo_muzicapp.R
import com.example.demo_muzicapp.data.model.Song
import com.example.demo_muzicapp.databinding.ItemSongBinding

class SongAdapter(
    private var songList: List<Song>,
    private val onClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(private val binding: ItemSongBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(song: Song) {
            binding.tvSong.text = song.title
            binding.tvArtist.text = song.artist

            // Cập nhật để lấy ảnh album từ file hoặc từ resources
            if (song.file.startsWith("/")) {
                // Nhạc cục bộ (MediaStore)
                Glide.with(binding.root.context)
                    .load(getAlbumArt(song.file))
                    .placeholder(R.drawable.bg_avatar_circle)
                    .error(R.drawable.bg_avatar_circle)
                    .into(binding.ivAlbumArt)
            } else {
                // Nhạc từ database (drawable name)
                val resId = binding.root.context.resources.getIdentifier(
                    song.image,
                    "drawable",
                    binding.root.context.packageName
                )
                binding.ivAlbumArt.setImageResource(if (resId != 0) resId else R.drawable.bg_avatar_circle)
            }

            binding.root.setOnClickListener {
                onClick(song)
            }
        }

        private fun getAlbumArt(path: String): ByteArray? {
            return try {
                val retriever = android.media.MediaMetadataRetriever()
                retriever.setDataSource(path)
                val art = retriever.embeddedPicture
                retriever.release()
                art
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int = songList.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songList[position])
    }

    fun updateList(newList: List<Song>) {
        songList = newList
        notifyDataSetChanged()
    }
}