package com.example.coffeeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
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

        loadUserLogin();
    }
    public void loadUserLogin()
    {
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
        SharedPreferences sharedPreferences = this.getSharedPreferences("remember login", Context.MODE_PRIVATE);
        if(!sharedPreferences.getString("username","").equals(""))
        {
            loadingBar.show();
            editTextUserName.setText(sharedPreferences.getString("username",""));
            editTextPassword.setText(sharedPreferences.getString("passsword",""));
            userName = sharedPreferences.getString("username","");
            password = sharedPreferences.getString("password","");
            doLogin(userName, password);
        }
        else{
            buttonLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingBar.show();
                    if(checkBoxRememberme.isChecked())
                        saveUserLogin();
                    userName = editTextUserName.getText().toString();
                    password = editTextPassword.getText().toString();
                    doLogin(userName, hash(password));
                }
            });
        }
    }
    public void saveUserLogin()
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences("remember login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", this.editTextUserName.getText().toString());
        editor.putString("password", hash(this.editTextPassword.getText().toString()));
        editor.apply();
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
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = false;
                User user = new User("","", "","");
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
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user", user);
                    loadingBar.dismiss();
                    finish();
                    LoginActivity.this.startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivity.this, "Username or Password is wrong!",Toast.LENGTH_LONG).show();
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
            MessageDigest messageDigest = java.security.MessageDigest.getInstance("MD5");
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
}