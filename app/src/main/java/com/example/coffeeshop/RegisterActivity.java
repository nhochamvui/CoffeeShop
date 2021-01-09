package com.example.coffeeshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName;
    private EditText etCity;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etDistrict;
    private Button btReg;
    private HttpRequestHelper httpRequestHelper;
    private Spinner editTextUserCity, editTextUserDistrict;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialComponent();
        btReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    public void initialComponent(){
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btReg = findViewById(R.id.btReg);
        httpRequestHelper = new HttpRequestHelper(getResources().getString(R.string.server_address));
    }
    public void register(){
        User user = new User();
        Map<String, String> location = new HashMap<String, String>();
        if (etName.getText().toString().equals("") || etPassword.getText().toString().equals("") || etEmail.getText().toString().equals("")){
            Toast.makeText(RegisterActivity.this, "Please fill the information", Toast.LENGTH_SHORT).show();
        }
        else{
            user.setName(etName.getText().toString());
            location.put("city", "");
            location.put("district", "");
            user.setEmail(etEmail.getText().toString());
            user.setPassword(etPassword.getText().toString());
            user.setLocation(location);
            String json = new Gson().toJson(user, User.class);
//            Log.e("json", json);
            Request postRequest = httpRequestHelper.getPostRequest("/register", json);
            new OkHttpClient().newCall(postRequest).enqueue(new Callback() {
                Handler handler = new Handler(RegisterActivity.this.getMainLooper());
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, "Failed to connect!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!response.isSuccessful()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    Toast.makeText(RegisterActivity.this, "Error: "+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    if (!jsonObject.getString("message").equals("")){
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.putExtra("justLoggedOut", 1);
                                        finish();
                                        RegisterActivity.this.startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(RegisterActivity.this, "Invalid format", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            });
        }
    }
}