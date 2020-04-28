package com.unitn.disi.lpsmt.happypuppy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.UserFriend;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserFriendService;
import com.unitn.disi.lpsmt.happypuppy.auth.SignIn;

import java.time.LocalDateTime;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserFriend userFriend = new UserFriend();
        userFriend.createdAt = LocalDateTime.now();

        Call<API.Response> call = API.getInstance().getClient().create(UserFriendService.class).update(UUID.fromString("23858604-8fe1-49a1-9b2a-0b15946d6c2b"), userFriend);
        call.enqueue(new Callback<API.Response>() {
            @Override
            public void onResponse(Call<API.Response> call, Response<API.Response> response) {

            }

            @Override
            public void onFailure(Call<API.Response> call, Throwable t) {
                System.err.println("[ERRORE]: " + t);
            }
        });

        Call<API.Response<UserFriend>> call2 = API.getInstance().getClient().create(UserFriendService.class).findById(UUID.fromString("23858604-8fe1-49a1-9b2a-0b15946d6c2b"));
        call2.enqueue(new Callback<API.Response<UserFriend>>() {
            @Override
            public void onResponse(Call<API.Response<UserFriend>> call, Response<API.Response<UserFriend>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserFriend userFriend = response.body().data;
                    System.out.println("[USER_FRIEND]: " + new Gson().toJson(userFriend));
                    System.out.println("[USER_FRIEND_CREATED_AT]: " + userFriend.createdAt);
                } else {
                    System.err.println("[CODE]: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<API.Response<UserFriend>> call, Throwable t) {
                System.err.println("[ERRORE]: " + t);
            }
        });

        /*User carlo = new User();
        carlo.name = "Carlo";
        carlo.surname = "Surname";
        carlo.username = "carletto2020";
        carlo.email = "carlocorradini@libero.net";
        carlo.phone = "+393382284181";
        carlo.password = "ciaocarlo";

        Call<API.Response<UUID>> call =  API.getInstance().client.create(UserService.class).create(carlo);



        call.enqueue(new Callback<API.Response<UUID>>() {
            @Override
            public void onResponse(Call<API.Response<UUID>> call, Response<API.Response<UUID>> response) {
                if(response.isSuccessful() && response.body() != null){
                    System.out.println("[INFO]: Success:" + response.body().data);

                    Call<API.Response<User>> findCarlo =  API.getInstance().client.create(UserService.class).findById(response.body().data);

                    findCarlo.enqueue(new Callback<API.Response<User>>() {
                        @Override
                        public void onResponse(Call<API.Response<User>> call, Response<API.Response<User>> response) {
                            if(response.isSuccessful() && response.body() != null) {
                                System.out.println("[INFO]: Success:" + response.body().data);
                            }
                            else{
                                switch (response.code()){
                                    case 409:
                                        System.err.println("Another user exists");
                                        break;
                                    case 422:
                                        System.err.println("Un processable entity");
                                        break;
                                    default:
                                        System.err.println("Default error");
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<API.Response<User>> call, Throwable t) {
                            System.err.println();
                        }
                    });

                } else{
                    switch (response.code()){
                        case 409:
                            System.err.println("Another user exists");
                            break;
                        case 422:
                            System.err.println("Un processable entity");
                            break;
                        default:
                            System.err.println("Default error");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<API.Response<UUID>> call, Throwable t) {
                System.err.println();
            }
        });
        /*Puppy puppy = new Puppy();
        puppy.name = "Anthony";
        puppy.gender = Puppy.Gender.MALE;
        puppy.specie = 2L;


        Call<API.Response<Long>> call = API.getInstance().client.create(PuppyService.class).create(puppy);

        call.enqueue(new Callback<API.Response<Long>>() {
            @Override
            public void onResponse(Call<API.Response<Long>> call, Response<API.Response<Long>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    System.out.println("[INFO]: Successo -> " + response.body().data);
                } else {
                    switch (response.code()) {
                        case 422: {
                            System.err.println("[ERROR]: Utente ha sbagliato qualcosa");
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<API.Response<Long>> call, Throwable t) {
                System.err.println("[ERROR]: " + t);
            }
        });

        Call<API.Response<Puppy>> callFindPuppy = API.getInstance().client.create(PuppyService.class).findById(74L);

        callFindPuppy.enqueue(new Callback<API.Response<Puppy>>() {
            @Override
            public void onResponse(Call<API.Response<Puppy>> call, Response<API.Response<Puppy>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    API.Response<Puppy> responsePuppy = response.body();
                    System.out.println("PUPPY!!!!: " + responsePuppy.data.specie);
                } else {

                }
            }

            @Override
            public void onFailure(Call<API.Response<Puppy>> call, Throwable t) {
            }
        });

        /*call.enqueue(new Callback<API.Response<Puppy>>() {
            @Override
            public void onResponse(@NotNull Call<API.Response<Puppy>> call, @NotNull Response<API.Response<Puppy>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Puppy puppy = response.body().data;
                    System.out.println("[INFO]: " + puppy.user + " | " + puppy.id);
                } else {
                    System.err.println("[ERROR]: " + new Gson().toJson(response.body()));
                }
            }

            @Override
            public void onFailure(@NotNull Call<API.Response<Puppy>> call, @NotNull Throwable t) {
                System.err.println("[ERROR]: " + t);
            }
        });*/

        /*Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(3000);  //Delay of 3 seconds
                } catch (Exception e) {

                } finally {
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        welcomeThread.start();*/
    }
}
