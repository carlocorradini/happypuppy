package com.unitn.disi.lpsmt.happypuppy.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.entity.UserFriend;
import com.unitn.disi.lpsmt.happypuppy.ui.components.CardViewAdapter;
import com.unitn.disi.lpsmt.happypuppy.ui.components.InfoCardView;
import com.unitn.disi.lpsmt.happypuppy.util.CardsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationsFragment subclass
 * @author Anthony Farina
 */
public class NotificationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private CardViewAdapter adapter;
    private TextView noNotifications;
    private List<UserFriend> friends;
    private SwipeRefreshLayout refreshNotifications;
    private LinearLayout loader;
    List<InfoCardView> cardList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications_fragment, null);
        noNotifications = view.findViewById(R.id.no_notifications);
        recyclerView = view.findViewById(R.id.notifications_recycler_view);
        refreshNotifications = view.findViewById(R.id.list_notifications_refresh);

        loader = view.findViewById(R.id.notifications_fragment_view_loader);

        refreshNotifications.setOnRefreshListener(() -> {
            refreshNotifications.setRefreshing(false);
            loadNotifications(view);
        });

        loadNotifications(view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

    /**
     * Load Notifications
     * @param view view
     */
    private void loadNotifications(View view) {
        friends = new ArrayList<>();
        cardList = new ArrayList<>();
        loader.setVisibility(View.VISIBLE);
        new CardsUtil.DownloadFriendRequests(friends -> {
            this.friends = friends;
            createCards(view);
        }).execute();
    }

    /**
     * create all cards for all notifications
     * @param view view
     */
    private void createCards(View view){
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).type.equals(UserFriend.Type.FRIEND_REQUEST)) {
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
                        recyclerView.setVisibility(View.GONE);
                    }else{
                        noNotifications.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }).execute(friends.get(i).friend);

            }else {
                noNotifications.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
        adapter = new CardViewAdapter(view.getContext(), cardList);
        recyclerView.setAdapter(adapter);
        loader.setVisibility(View.GONE);
    }
}
