package com.unitn.disi.lpsmt.happypuppy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.AuthManager;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;
import com.unitn.disi.lpsmt.happypuppy.api.entity.User;
import com.unitn.disi.lpsmt.happypuppy.api.entity.error.ConflictError;
import com.unitn.disi.lpsmt.happypuppy.api.entity.error.UnprocessableEntityError;
import com.unitn.disi.lpsmt.happypuppy.api.service.PuppyService;
import com.unitn.disi.lpsmt.happypuppy.api.service.UserService;
import com.unitn.disi.lpsmt.happypuppy.auth.SignIn;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        User user = new User();
        user.username = "carlocorradini";
        user.password = "password";
        user.email = "carlo.ita98@gmail.com";
        user.phone = "+393273679553";

        Call<API.Response<UUID>> call = API.getInstance().getClient().create(UserService.class).create(user);

        call.enqueue(new Callback<API.Response<UUID>>() {
                         @Override
                         public void onResponse(Call<API.Response<UUID>> call, Response<API.Response<UUID>> response) {
                             if (response.isSuccessful() && response.body() != null) {
                                 System.out.println("[INFO]: " + response.body().data);
                             } else if (response.errorBody() != null) {
                                 switch (response.code()) {
                                     case HttpStatus.SC_UNPROCESSABLE_ENTITY: {
                                         API.Response<List<UnprocessableEntityError>> error = API.ErrorConverter.convert(response.errorBody(), new TypeToken<API.Response<List<UnprocessableEntityError>>>(){}.getType());
                                         System.err.println("[ERROR]: " + error.data.get(0).property);
                                         break;
                                     }
                                     case HttpStatus.SC_CONFLICT: {
                                         API.Response<List<ConflictError>> error = API.ErrorConverter.convert(response.errorBody(), new TypeToken<API.Response<List<ConflictError>>>(){}.getType());
                                         System.err.println("[ERROR]: " + error.data.get(0).property);
                                         break;
                                     }
                                     default: {
                                         System.err.println("???????????????");
                                         break;
                                     }
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<API.Response<UUID>> call, Throwable t) {

                         }
                     }
        );


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
        });*/
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
                    sleep(2500);  //Delay of 3 seconds
                } catch (Exception e) {
                }
                finally {
                    AuthManager.getInstance().clearToken(); /* TODO: remove this line: only for testing */
        if (AuthManager.getInstance().isAuth()) {
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
            finish();
        }
                    /*
                }
            }
        };
        welcomeThread.start();*/
    }
}
