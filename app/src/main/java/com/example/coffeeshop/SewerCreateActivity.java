package com.example.coffeeshop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SewerCreateActivity extends AppCompatActivity {

    private EditText editTextSewerName, editTextSewerDesc, editTextSewerCategory, editTextSewerLocationDistrict, editTextSewerLocationCity, editTextSewerChannel;
    private Button buttonCancel, buttonSewerCreate;
    private User2 user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewer_create);
        initialComponents();
    }

    private void initialComponents() {
        user = (User2) getIntent().getSerializableExtra("User");
        editTextSewerName = findViewById(R.id.editTextCreateSewerName);
        editTextSewerDesc = findViewById(R.id.editTextCreateSewerDesc);
        editTextSewerLocationDistrict = findViewById(R.id.editTextCreateSewerLocationDistrict);
        editTextSewerLocationCity = findViewById(R.id.editTextCreateSewerLocationCity);
        buttonCancel = findViewById(R.id.buttonSewerCreateCancel);
        buttonSewerCreate = findViewById(R.id.buttonSewerCreateInfo);
        buttonSewerCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewSewer();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SewerCreateActivity.this.finish();
            }
        });
    }

    private void createNewSewer() {
        Sewer sewer = new Sewer();
        sewer.setName(editTextSewerName.getText().toString());
        sewer.setDescription(editTextSewerDesc.getText().toString());
        Map<String, String> location = new HashMap<>();
        location.put("city", editTextSewerLocationCity.getText().toString());
        location.put("district", editTextSewerLocationDistrict.getText().toString());
        sewer.setLocation(location);

        if(sewer.isValidForCreate()){
            HttpRequestHelper requestHelper = new HttpRequestHelper(getResources().getString(R.string.server_address));
            String json = new Gson().toJson(sewer, Sewer.class);
            Request postRequest = requestHelper.getPostRequest("/sewers", json, user.getAccessToken());
            new OkHttpClient().newCall(postRequest).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                if (!response.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Error: "+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    SewerCreateActivity.this.finish();
                                    Toast.makeText(getApplicationContext(), "Successful: "+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });
        }

    }
}