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
        System.out.println("ON BIND VIEW");
        new ImageUtil.DownloadImage(avatar -> {
            if (avatar == null) return;
            Bitmap avt = Bitmap.createScaledBitmap(avatar, USER_MARKER_SIZE.getLeft(), USER_MARKER_SIZE.getRight(), false);
            holder.image.setImageBitmap(avt);
        }).execute(profile.getImage());
        holder.username.setText(profile.getUsername());
        holder.name.setText(profile.getName());
        if(profile.equals("")){
            holder.age.setText("");
        }else{
            holder.age.setText(profile.getAge());
        }
        holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ProfilePuppy.class);
                intent.putExtra("id_puppy", profile.getIdPuppy().toString());
                ctx.startActivity(intent);
        });
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
