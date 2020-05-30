package com.unitn.disi.lpsmt.happypuppy.ui.components;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy.ProfilePuppy;
import com.unitn.disi.lpsmt.happypuppy.ui.profile.user.ProfileUser;

import java.util.List;

/**
 *  CardViewAdapter
 *  @author Anthony Farina
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder>{
    private Context ctx;
    private List<InfoCardView> profiles;

    /**
     *
     * @param ctx context
     * @param profiles list of profiles
     */
    public CardViewAdapter(Context ctx, List<InfoCardView> profiles) {
        this.ctx = ctx;
        this.profiles = profiles;
    }

    /**
     *
     * @param parent parent
     * @param viewType viewType
     * @return constructor
     */
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.card_view_fragment, parent , false);

        return new CardViewHolder(view);
    }

    /**
     *
     * @param holder holder
     * @param position position of array
     */
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

    /**
     *
     * @return size of the array
     */
    @Override
    public int getItemCount() {
        return profiles.size();
    }

    /**
     *  CardViewHolder class
     */
    static class CardViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView username;
        TextView name;
        TextView age;

        /**
         *
         * @param itemView cardItem
         */
        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.card_view_fragment_avatar_profile);
            username = itemView.findViewById(R.id.text_view_card_username);
            name = itemView.findViewById(R.id.text_view_card_full_name);
            age = itemView.findViewById(R.id.text_view_card_age);
        }
    }
}
