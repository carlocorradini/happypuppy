package com.unitn.disi.lpsmt.happypuppy.ui.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy.ProfilePuppy;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.user.ProfileUser;
import com.unitn.disi.lpsmt.happypuppy.util.ImageUtil;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder>{
    /**
     * {@link User} {@link Marker} size
     */
    private static final Pair<Integer, Integer> USER_MARKER_SIZE = Pair.of(128, 128);
    private Context ctx;
    private List<InfoCardView> profiles;

    public CardViewAdapter(Context ctx, List<InfoCardView> profiles) {
        this.ctx = ctx;
        this.profiles = profiles;
    }
    public void clearApplications() {
        int size = this.profiles.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                profiles.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.card_view_fragment, parent , false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        InfoCardView profile = profiles.get(position);
        Picasso.get().load(String.valueOf(profile.getImage())).into(holder.image);
        holder.username.setText(profile.getUsername());
        holder.name.setText(profile.getName());
        if(profile.getAge().equals("")){
            holder.age.setText("");
        }else{
            holder.age.setText(profile.getAge());
        }
        if(profile.getIdPuppy() != null) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ProfilePuppy.class);
                intent.putExtra("id_puppy", profile.getIdPuppy().toString());
                ctx.startActivity(intent);
            });
        }else{
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ProfileUser.class);
                intent.putExtra("uuid_user", profile.getUuid().toString());
                ctx.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView username;
        TextView name;
        TextView age;


        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.card_view_fragment_avatar_profile);
            username = itemView.findViewById(R.id.text_view_card_username);
            name = itemView.findViewById(R.id.text_view_card_full_name);
            age = itemView.findViewById(R.id.text_view_card_age);
        }
    }
}
