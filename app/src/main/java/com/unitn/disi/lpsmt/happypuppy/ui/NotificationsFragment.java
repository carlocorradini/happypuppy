package com.unitn.disi.lpsmt.happypuppy.ui;

import android.icu.text.IDNA;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.entity.UserFriend;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserFriendService;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;
import com.unitn.disi.lpsmt.happypuppy.ui.components.CardViewAdapter;
import com.unitn.disi.lpsmt.happypuppy.ui.components.InfoCardView;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;
import com.unitn.disi.lpsmt.happypuppy.util.CardsUtil;
import com.unitn.disi.lpsmt.happypuppy.util.UserUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private CardViewAdapter adapter;
    private RelativeLayout noNotifications;
    private List<UserFriend> friends;
    List<InfoCardView> cardList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications_fragment, null);
        noNotifications = view.findViewById(R.id.no_notifications);
        recyclerView = view.findViewById(R.id.notifications_recycler_view);
        friends = new ArrayList<>();
        cardList = new ArrayList<>();

        loadNotifications(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }
    private void loadNotifications(View view) {
        new CardsUtil.DownloadFriendRequests(friends -> {
            this.friends = friends;
            createCards(view);
        }).execute();
    }

    private void createCards(View view){
        for (int i = 0; i < friends.size(); i++) {
            System.out.println("How many friends are there? "+friends.size());
            if (friends.get(i).type.equals(UserFriend.Type.FRIEND_REQUEST)) {
                System.out.println("We could be friends: "+friends.get(i).type.toString());
                new CardsUtil.DownloadCardUser(user -> {
                    String name, surname;
                    if(user.name == null || user.surname == null){
                        name = "";
                        surname = "";
                    }else{
                        name = user.name;
                        surname = user.surname;
                    }
                    InfoCardView tmp = new InfoCardView(user.avatar, user.username, name + " " + surname, "", user.id);
                    cardList.add(tmp);
                    adapter = new CardViewAdapter(view.getContext(), cardList);
                    recyclerView.setAdapter(adapter);
                    if(cardList.size() == 0){
                        noNotifications.setVisibility(View.VISIBLE);
                    }
                }).execute(friends.get(i).friend);
            }
        }
    }
}
