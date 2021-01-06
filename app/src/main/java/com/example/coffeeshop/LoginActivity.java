package com.example.coffeeshop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private EditText editTextUserName;
    private EditText editTextPassword;
    private Button buttonLogin;
    private DatabaseReference mDatabase;
    private String userName, password;
    private CheckBox checkBoxRememberme;
    private ProgressDialog loadingBar;
    private HttpRequestHelper httpRequestHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialComponent();
        int state = getIntent().getIntExtra("justLoggedOut", 0);
        if(state == 1)
            justLoggedOut();
        else
            loadUserLogin();
    }

    public void justLoggedOut() {
        setupLoadingBar();
        sharedPreferences = this.getSharedPreferences("remember login", Context.MODE_PRIVATE);
        editTextUserName.setText(sharedPreferences.getString("username", ""));
        editTextPassword.setText(sharedPreferences.getString("password", ""));
        if(sharedPreferences.getInt("rememberMe", 0) == 1)
            checkBoxRememberme.setChecked(true);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.show();
                if (checkBoxRememberme.isChecked())
                    savedUserLogin();
                else
                    unsavedUserLogin();
                userName = editTextUserName.getText().toString();
                password = editTextPassword.getText().toString();
//                doLogin(userName, hash(password));
                doLogin(userName, password);
            }
        });
    }

    public void loadUserLogin() {
        setupLoadingBar();
        sharedPreferences = this.getSharedPreferences("remember login", Context.MODE_PRIVATE);
        if(!sharedPreferences.getString("username","").equals(""))
        {
            loadingBar.show();
            editTextUserName.setText(sharedPreferences.getString("username",""));
            editTextPassword.setText(sharedPreferences.getString("password",""));
            userName = sharedPreferences.getString("username","");
            password = sharedPreferences.getString("password","");
            doLogin(userName, password);
        }
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.show();
                if(checkBoxRememberme.isChecked())
                    savedUserLogin();
                userName = editTextUserName.getText().toString();
                password = editTextPassword.getText().toString();
                doLogin(userName, password);
            }
        });
    }
    public void savedUserLogin()
    {
        sharedPreferences = this.getSharedPreferences("remember login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("rememberMe", 1);
        editor.putString("username", this.editTextUserName.getText().toString());
        editor.putString("password", this.editTextPassword.getText().toString());
        editor.commit();
    }
    public void unsavedUserLogin(){
        sharedPreferences = this.getSharedPreferences("remember login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("rememberMe", 0);
        editor.putString("username","");
        editor.putString("password", "");
        editor.commit();
    }
    private void initialComponent()
    {
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUserName = findViewById(R.id.editTextUserName_Add);
        buttonLogin = findViewById(R.id.buttonLogin);
        checkBoxRememberme = findViewById(R.id.checkBoxRememberMe);
        httpRequestHelper = new HttpRequestHelper(getResources().getString(R.string.server_address));
    }
    private void doLogin(final String email, final String password) {
        Map<String, String> content = new HashMap<String, String>();
        content.put("email", email);
        content.put("password", password);

        Request postRequest = httpRequestHelper.getPostRequest("/login", content);
        new OkHttpClient().newCall(postRequest).enqueue(new Callback() {
            Handler handler = new Handler(LoginActivity.this.getMainLooper());
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Failed to connect!", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(LoginActivity.this, "Error: "+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            loadingBar.dismiss();
                        } else {
                            User user = null;
                            try {
                                String json = response.body().string();
                                user = new Gson().fromJson(json, User.class);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("display_name", user.getName());
                            editor.commit();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("User", user);
                            loadingBar.dismiss();
                            finish();
                            LoginActivity.this.startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    private String hash(String s)
    {
        StringBuilder pwdHex = new StringBuilder();
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(s.getBytes());
            byte[] messageDigests = messageDigest.digest();
            for(byte tempMess : messageDigests)
            {
                String h = Integer.toHexString(0xFF & tempMess);
                while(h.length()<2)
                {
                    h = "0" +h;
                }
                pwdHex.append(h);
            }
            return pwdHex.toString();
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    public void setupLoadingBar(){
        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Logging in");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
    }
}