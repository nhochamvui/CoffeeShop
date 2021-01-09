package com.example.coffeeshop;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SewerCreateActivity extends AppCompatActivity {

    private EditText editTextSewerName, editTextSewerDesc;
    private ImageView backButton;
    private Button buttonCancel, buttonSewerCreate;
    private AutoCompleteTextView autoCompleteTextViewSewerCity, autoCompleteTextViewSewerDistrict;
    private User user;
    private TextView textViewTitleSewerActivity;
    private final ArrayList<String> cities = new ArrayList<>();
    private final ArrayList<String> districts = new ArrayList<>();
    private ArrayAdapter cityAdapter, districtAdapter;
    private ArrayList<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewer_edit);
        initialComponents();
    }

    private void initialComponents() {
        user = (User) getIntent().getSerializableExtra("User");
        backButton = findViewById(R.id.imageViewSewerEditBackButton);
        textViewTitleSewerActivity = findViewById(R.id.textViewTitleSewerActivity);
        autoCompleteTextViewSewerCity = findViewById(R.id.my_spinner_dropdown_sewer_city);
        autoCompleteTextViewSewerDistrict = findViewById(R.id.my_spinner_dropdown_sewer_district);

        cityAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextViewSewerCity.setAdapter(cityAdapter);

        districtAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, districts);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextViewSewerDistrict.setAdapter(districtAdapter);

        fetchLocation();

        autoCompleteTextViewSewerCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteTextViewSewerDistrict.setText("", false);
                fetchDistrict(position, "");
            }
        });

        editTextSewerName = findViewById(R.id.editTextSewerName);
        editTextSewerDesc = findViewById(R.id.editTextSewerDesc);
        buttonCancel = findViewById(R.id.buttonSewerEditCancel);
        buttonSewerCreate = findViewById(R.id.buttonUpdateSewerInfo);
        buttonSewerCreate.setText("Create");
        textViewTitleSewerActivity.setText("Create");
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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SewerCreateActivity.this.finish();
            }
        });
    }

    private void createNewSewer() {
        Sewer sewer = new Sewer();
        sewer.setName(editTextSewerName.getText().toString());
        sewer.setDescription(editTextSewerDesc.getText().toString());
        Map<String, String> location = new HashMap<>();
        location.put("city", autoCompleteTextViewSewerCity.getText().toString());
        location.put("district", autoCompleteTextViewSewerDistrict.getText().toString());
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


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void fetchDistrict(int position, String district){
        for(Location location : locations){
            if(location.getName().equals(cities.get(position))){
                districts.clear();
                for(Map<String, String> tempDistrict : location.getDistrict()){
                    districts.add(tempDistrict.get("name"));
                }
                districtAdapter.notifyDataSetChanged();
                break;
            }
        }
        if(!district.equals("")){
            autoCompleteTextViewSewerDistrict.setText(districts.get(districts.indexOf(district)), false);
        }
    }

    private void fetchLocation() {
        Request getRequest = MainActivity.httpRequestHelper.getGetRequest("/locations", MainActivity.user.getAccessToken());
        new OkHttpClient().newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if(this!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SewerCreateActivity.this, "Failed to fetch location due to connection!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String json = response.body().string();
                if(this!=null){
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void run() {
                            try {
                                if(!response.isSuccessful()){
                                    JSONObject jsonObject = new JSONObject(json);
                                    Toast.makeText(SewerCreateActivity.this, "Error: "+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Type listType = new TypeToken<ArrayList<Location>>(){}.getType();
                                    locations = new Gson().fromJson(json, listType);
                                    cities.clear();
                                    for(Location temp:locations){
                                        cities.add(temp.getName());
                                    }
                                    cityAdapter.notifyDataSetChanged();
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        });
    }
}