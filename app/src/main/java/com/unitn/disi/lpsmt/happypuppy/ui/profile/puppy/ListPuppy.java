package com.unitn.disi.lpsmt.happypuppy.ui.profile.puppy;

import android.os.Bundle;
import android.util.Log;
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
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;
import com.unitn.disi.lpsmt.happypuppy.api.service.PuppyService;
import com.unitn.disi.lpsmt.happypuppy.ui.components.CardViewAdapter;
import com.unitn.disi.lpsmt.happypuppy.ui.components.InfoCardView;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ListPuppy class
 * @author Anthony Farina
 */
public class ListPuppy extends Fragment {
    private RecyclerView recyclerView;
    private CardViewAdapter adapter;
    private SwipeRefreshLayout refresh;
    private TextView noPuppies;
    private LinearLayout loader;
    private UUID user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_puppies_fragment, null);
        loader = view.findViewById(R.id.list_puppies_fragment_view_loader);

        loadPuppies(view);
        recyclerView = view.findViewById(R.id.list_puppies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        refresh = view.findViewById(R.id.list_puppies_refresh);
        noPuppies = view.findViewById(R.id.no_puppies);

        refresh.setOnRefreshListener(() -> {
            refresh.setRefreshing(false);
            loadPuppies(view);
        });

        return view;
    }

    /**
     * ListPuppy constructor for Authenticated User
     */
    public ListPuppy(){
        this.user = AuthManager.getInstance().getAuthUserId();
    }

    /**
     * ListPuppy constructor for generic user
     * @param uuid_user user
     */
    public ListPuppy(final UUID uuid_user){
        this.user = uuid_user;
    }

    /**
     * LoadPuppies create alla cards for list Puppies
     * @param view view
     */
    public void loadPuppies(View view){
        loader.setVisibility(View.VISIBLE);
        List<InfoCardView> cardList = new ArrayList<>();
        try {
            Call<API.Response<List<Puppy>>> call = API.getInstance().getClient().create(PuppyService.class).findByUser(this.user);
            call.enqueue(new Callback<API.Response<List<Puppy>>>() {
                @Override
                public void onResponse(@NotNull Call<API.Response<List<Puppy>>> call, @NotNull Response<API.Response<List<Puppy>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Puppy> puppies = response.body().data;
                        for (int i = 0; i < puppies.size(); i++) {
                            int age = -1;
                            String textAge = "";
                            if (puppies.get(i).dateOfBirth != null) {
                                LocalDate now = LocalDate.now();
                                age = now.getYear() - puppies.get(i).dateOfBirth.getYear();
                                textAge = age+" "+getResources().getString(R.string.year);
                                if(age == 0){
                                    age = now.getMonthValue() - puppies.get(i).dateOfBirth.getMonthValue();
                                    textAge = age+" "+getResources().getString(R.string.month);
                                }
                            }else{
                                textAge = getResources().getString(R.string.unknown);
                            }
                            String specie = getResources().getStringArray(R.array.animal_kinds)[Integer.parseInt(puppies.get(i).specie.toString())-1];
                            InfoCardView tmp = new InfoCardView(puppies.get(i).avatar, puppies.get(i).name, specie , textAge, puppies.get(i).id);
                            cardList.add(tmp);
                        }
                        adapter = new CardViewAdapter(view.getContext(), cardList);
                        recyclerView.setAdapter(adapter);
                        if(cardList.size() == 0){
                            noPuppies.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }else{
                            noPuppies.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                    loader.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NotNull Call<API.Response<List<Puppy>>> call, @NotNull Throwable t) {
                    new Toasty(view.getContext(), view, R.string.error_unknown);
                }
            });
        }catch(Exception e){
            Log.e("ErrList","Cannot load puppies due to exception: "+e.getMessage());
        }
    }
}
