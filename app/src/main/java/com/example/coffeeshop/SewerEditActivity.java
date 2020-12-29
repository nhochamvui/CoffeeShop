package com.example.coffeeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SewerEditActivity extends AppCompatActivity {

    private EditText editTextSewerName, editTextSewerDesc, editTextSewerStatus, editTextSewerCategory, editTextSewerLocation, editTextSewerChannel, editTextSewerId;
    private Button buttonUpdateSewerInfo;
    private Sewer sewer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewer_edit);
        initialComponent();
    }

    private void initialComponent() {
        //initialize components
        sewer = (Sewer) getIntent().getSerializableExtra("Sewer");
        editTextSewerName = findViewById(R.id.editTextSewerName);
        editTextSewerDesc = findViewById(R.id.editTextSewerDesc);
        editTextSewerStatus = findViewById(R.id.editTextSewerStatus);
        editTextSewerCategory = findViewById(R.id.editTextSewerCategory);
        editTextSewerLocation = findViewById(R.id.editTextSewerLocation);
        editTextSewerChannel = findViewById(R.id.editTextSewerChannel);
        editTextSewerId = findViewById(R.id.editTextSewerId);
        buttonUpdateSewerInfo = findViewById(R.id.buttonUpdateSewerInfo);
        // set text
        editTextSewerName.setText(sewer.getName());
        editTextSewerDesc.setText(sewer.getDesc());
//        editTextSewerStatus.setText(sewer.getStatus());
        editTextSewerCategory.setText(sewer.getCategory());
        editTextSewerLocation.setText(sewer.getLocation());
        editTextSewerChannel.setText(sewer.getChannel());
        editTextSewerId.setText(sewer.getId());
        editTextSewerId.setEnabled(false);
        // listen to button update
        buttonUpdateSewerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSewer(sewer);
            }
        });
    }

    private void editSewer(final Sewer sewer){
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("sewer");
        sewer.setName(editTextSewerName.getText().toString());
        sewer.setDesc(editTextSewerDesc.getText().toString());
//        sewer.setStatus(editTextSewerStatus.getText().toString());
        sewer.setCategory(editTextSewerCategory.getText().toString());
        sewer.setLocation(editTextSewerLocation.getText().toString());
        sewer.setChannel(editTextSewerChannel.getText().toString());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot id : dataSnapshot.getChildren())
                {
                    if(id.child("id").getValue().equals(sewer.getId())) {
                        mDatabase.child(id.getKey()).setValue(sewer).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Chỉnh sửa thành công", Toast.LENGTH_SHORT).show();
                                SewerEditActivity.this.finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}