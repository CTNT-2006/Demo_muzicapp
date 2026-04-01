package com.example.demo_muzicapp;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    List<String> list;
    Context context;

    public SearchAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSong, tvArtist;
        ImageView ivAlbumArt, ivMore;

        public ViewHolder(View view) {
            super(view);
            tvSong = view.findViewById(R.id.tvSong);
            tvArtist = view.findViewById(R.id.tvArtist);
            ivAlbumArt = view.findViewById(R.id.ivAlbumArt);
            ivMore = view.findViewById(R.id.ivMore);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String song = list.get(position);

        holder.tvSong.setText(song);
        holder.tvArtist.setText("Unknown Artist");

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "CLICK OK", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("title", song);
            intent.putExtra("song",
                    "android.resource://" + context.getPackageName() + "/" + R.raw.song1
            );

            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}