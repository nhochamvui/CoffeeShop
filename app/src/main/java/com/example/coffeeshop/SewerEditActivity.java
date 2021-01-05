package com.example.coffeeshop;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SewerEditActivity extends AppCompatActivity {

    private TextInputEditText editTextSewerName, editTextSewerDesc, editTextSewerLocationDistrict, editTextSewerLocationCity, editTextSewerChannel;
    private Button buttonUpdateSewerInfo, buttonSewerEditCancel;
    private ImageView imageViewSewerEditBackButton;
    private AutoCompleteTextView autoCompleteTextViewSewerCategory;
    private Sewer sewer;
    private Intent intent;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewer_edit);
        initialComponent();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initialComponent() {
        //initialize components
        intent = getIntent();
        sewer = (Sewer) intent.getSerializableExtra("Sewer");
        editTextSewerName = findViewById(R.id.editTextSewerName);
        editTextSewerDesc = findViewById(R.id.editTextSewerDesc);
        editTextSewerLocationDistrict = findViewById(R.id.editTextSewerLocationDistrict);
        editTextSewerLocationCity = findViewById(R.id.editTextSewerLocationCity);
        editTextSewerChannel = findViewById(R.id.editTextSewerChannel);
        editTextSewerName.setText(sewer.getName());
        editTextSewerDesc.setText(sewer.getDescription());
        editTextSewerLocationDistrict.setText(sewer.getLocation().get("district"));
        editTextSewerLocationCity.setText(sewer.getLocation().get("city"));
        editTextSewerChannel.setText(sewer.getId());

        imageViewSewerEditBackButton = findViewById(R.id.imageViewSewerEditBackButton);
        buttonSewerEditCancel = findViewById(R.id.buttonSewerEditCancel);
        autoCompleteTextViewSewerCategory = findViewById(R.id.my_spinner_dropdown_sewer_category);
        buttonUpdateSewerInfo = findViewById(R.id.buttonUpdateSewerInfo);
        List<String> categories = new ArrayList<String>();
        categories.add("Rất nhỏ");categories.add("Nhỏ");categories.add("Vừa");categories.add("Tương đối lớn");categories.add("Lớn");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextViewSewerCategory.setAdapter(dataAdapter);
//        autoCompleteTextViewSewerCategory.setText(sewer.getCategory(), false);
        imageViewSewerEditBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SewerEditActivity.this.finish();
            }
        });
        buttonSewerEditCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SewerEditActivity.this.finish();
            }
        });
        buttonUpdateSewerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextSewerName.getText().toString().equals("") || editTextSewerChannel.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Name and Channel are required", Toast.LENGTH_LONG).show();
                }
                else{
                    editSewer(sewer);
                }
            }
        });
    }

    private void editSewer(final Sewer sewer){
        sewer.setId(editTextSewerChannel.getText().toString());
        sewer.setName(editTextSewerName.getText().toString());
        sewer.setDescription(editTextSewerDesc.getText().toString().equals(null) ? "" : editTextSewerDesc.getText().toString());
        Map<String, String> location = new HashMap<>();
        location.put("city", editTextSewerLocationCity.getText().toString());
        location.put("district", editTextSewerLocationDistrict.getText().toString());
        sewer.setLocation(location);
        if(sewer.isValidForEdit()){
            HttpRequestHelper requestHelper = new HttpRequestHelper(getResources().getString(R.string.server_address));
            Request editRequest = requestHelper.getEditRequest("/sewers", sewer.getId(), new Gson().toJson(sewer, Sewer.class), MainActivity.user.getAccessToken());
            new OkHttpClient().newCall(editRequest).enqueue(new Callback() {
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
                                    SewerEditActivity.this.finish();
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