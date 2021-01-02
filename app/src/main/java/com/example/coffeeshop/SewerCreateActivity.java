package com.example.coffeeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SearchEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SewerCreateActivity extends AppCompatActivity {

    private EditText editTextSewerName, editTextSewerDesc, editTextSewerCategory, editTextSewerLocation, editTextSewerChannel;
    private Button buttonCancel, buttonSewerCreate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewer_create);
        initialComponents();
    }

    private void initialComponents() {
        editTextSewerName = findViewById(R.id.editTextCreateSewerName);
        editTextSewerDesc = findViewById(R.id.editTextCreateSewerDesc);
        editTextSewerCategory = findViewById(R.id.editTextCreateSewerCategory);
        editTextSewerLocation = findViewById(R.id.editTextCreateSewerLocation);
        editTextSewerChannel = findViewById(R.id.editTextCreateSewerChannel);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSewerCreate = findViewById(R.id.buttonSewerCreate);
        buttonSewerCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextSewerName.getText().toString().equals("") || editTextSewerChannel.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Name and Channel are required!", Toast.LENGTH_SHORT).show();
                }
                else {
                    createNewSewer();
                }
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
        sewer.setCategory(editTextSewerName.getText().toString());
        sewer.setLocation(editTextSewerLocation.getText().toString());
        sewer.setChannel(editTextSewerChannel.getText().toString());
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("sewer");
        final DatabaseReference newSewerRef = mDatabase.push();
        sewer.setId(newSewerRef.getKey());
        newSewerRef.setValue(sewer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SewerCreateActivity.this.finish();
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi: "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}