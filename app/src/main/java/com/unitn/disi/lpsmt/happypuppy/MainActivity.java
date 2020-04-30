package com.unitn.disi.lpsmt.happypuppy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.entity.error.UnprocessableEntityError;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User user = new User();

        Call<API.Response<UUID>> call = API.getInstance().getClient().create(UserService.class).create(user);

        call.enqueue(new Callback<API.Response<UUID>>() {
            @Override
            public void onResponse(Call<API.Response<UUID>> call, Response<API.Response<UUID>> response) {
                if (response.errorBody() != null) {
                    API.Response<List<UnprocessableEntityError>> error = API.ErrorConverter.convert(response.errorBody());
                    System.out.println("[INFO]: " + new Gson().toJson(error.data.size()));
                }
            }

            @Override
            public void onFailure(Call<API.Response<UUID>> call, Throwable t) {

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
