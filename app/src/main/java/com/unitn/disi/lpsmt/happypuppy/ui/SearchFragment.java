package com.unitn.disi.lpsmt.happypuppy.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unitn.disi.lpsmt.happypuppy.R;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;
import com.unitn.disi.lpsmt.happypuppy.ui.components.CardViewAdapter;
import com.unitn.disi.lpsmt.happypuppy.ui.components.InfoCardView;
import com.unitn.disi.lpsmt.happypuppy.ui.components.Toasty;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private CardViewAdapter adapter;
    private TextView noResults;
    private EditText inputSearch;
    private TextView resultsSearch;
    private LinearLayout loader;
    List<InfoCardView> cardList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, null);
        noResults = view.findViewById(R.id.no_result);
        recyclerView = view.findViewById(R.id.search_recycler_view);
        inputSearch = view.findViewById(R.id.search_input_text);
        resultsSearch = view.findViewById(R.id.number_results);
        loader = view.findViewById(R.id.search_fragment_view_loader);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loader.setVisibility(View.VISIBLE);
                cardList = new ArrayList<>();
                if (!inputSearch.getText().toString().isEmpty()) {
                    Map<String, String> searchUsers = new HashMap<>();
                    searchUsers.put("username", inputSearch.getText().toString());
                    try{
                        Call<API.Response<List<User>>> call = API.getInstance().getClient().create(UserService.class).find(searchUsers);
                        call.enqueue(new Callback<API.Response<List<User>>>() {
                            @Override
                            public void onResponse(@NotNull Call<API.Response<List<User>>> call, @NotNull Response<API.Response<List<User>>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<User> results = response.body().data;
                                    for (int i = 0; i < results.size(); i++) {
                                        User user = results.get(i);
                                        String name;
                                        if (user.name == null) {
                                            name = "";
                                        } else {
                                            name = user.name;
                                        }
                                        InfoCardView tmp = new InfoCardView(user.avatar, user.username, name, "", user.id);
                                        cardList.add(tmp);
                                        noResults.setVisibility(View.GONE);
                                        resultsSearch.setText(getResources().getString(R.string.resultsSearch)
                                                .concat(" ").concat(String.valueOf(cardList.size())));
                                        resultsSearch.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                    }
                                    if(cardList.size() == 0){
                                        noResults.setVisibility(View.VISIBLE);
                                        resultsSearch.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.GONE);
                                        adapter = new CardViewAdapter(view.getContext(), cardList);
                                        recyclerView.setAdapter(adapter);
                                    }
                                    adapter = new CardViewAdapter(view.getContext(), cardList);
                                    recyclerView.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<API.Response<List<User>>> call, @NotNull Throwable t) {
                                new Toasty(view.getContext(), view, R.string.error_unknown);
                            }
                        });
                    }catch(Exception e){
                        Log.e("ErrList","Cannot load users due to exception: "+e.getMessage());
                    }
                }else{
                    noResults.setVisibility(View.VISIBLE);
                    resultsSearch.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    adapter = new CardViewAdapter(view.getContext(), cardList);
                    recyclerView.setAdapter(adapter);
                }
                loader.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }
}
