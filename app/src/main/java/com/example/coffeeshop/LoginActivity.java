package com.example.coffeeshop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private EditText editTextUserName;
    private EditText editTextPassword;
    private Button buttonLogin;
    private DatabaseReference mDatabase;
    private String userName, password;
    private CheckBox checkBoxRememberme;
    private ProgressDialog loadingBar;

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
        editTextPassword.setText(sharedPreferences.getString("passsword", ""));
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
                doLogin(userName, hash(password));
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
            editTextPassword.setText(sharedPreferences.getString("passsword",""));
            userName = sharedPreferences.getString("username","");
            password = sharedPreferences.getString("password","");
            doLogin(userName, password);
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingBar.show();
                    if(checkBoxRememberme.isChecked())
                        savedUserLogin();
                    userName = editTextUserName.getText().toString();
                    password = editTextPassword.getText().toString();
                    doLogin(userName, hash(password));
                }
            });
        }
        else{
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingBar.show();
                    if(checkBoxRememberme.isChecked())
                        savedUserLogin();
                    userName = editTextUserName.getText().toString();
                    password = editTextPassword.getText().toString();
                    doLogin(userName, hash(password));
                }
            });
        }
    }
    public void savedUserLogin()
    {
        sharedPreferences = this.getSharedPreferences("remember login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("rememberMe", 1);
        editor.putString("username", this.editTextUserName.getText().toString());
        editor.putString("password", hash(this.editTextPassword.getText().toString()));
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
    }
    private void doLogin(final String userName, final String password) {
        String pwd = new String("");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        sharedPreferences = this.getSharedPreferences("chat room", Context.MODE_PRIVATE);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = false;
                User user = new User("", "", "", "","", false, false, false);
                Log.e("check login","username "+userName + " hash pwd: "+(password));

                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    if (id.child("username").getValue().equals(userName) && id.child("password").getValue().equals((password)))
                    {
                        user = id.getValue(User.class);
                        flag = true;
                        break;
                    }
                }
                if(flag)
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("display_name", user.getDisplayname());
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user", user);
                    loadingBar.dismiss();
                    finish();
                    LoginActivity.this.startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivity.this, "Username or Password is wrong!",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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