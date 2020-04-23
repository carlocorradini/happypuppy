package com.unitn.disi.lpsmt.happypuppy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.unitn.disi.lpsmt.happypuppy.api.API;
import com.unitn.disi.lpsmt.happypuppy.api.entity.Puppy;
import com.unitn.disi.lpsmt.happypuppy.api.service.PuppyService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Puppy puppy = new Puppy();
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
                    Intent intent = new Intent(MainActivity.this, RegisterPuppy.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        welcomeThread.start();*/
    }

}
