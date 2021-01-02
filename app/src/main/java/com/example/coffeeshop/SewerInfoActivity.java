package com.example.coffeeshop;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SewerInfoActivity extends AppCompatActivity {

    private TextInputEditText editTextSewerName, editTextSewerDesc, editTextSewerLocation, editTextSewerChannel;
    private Button buttonUpdateSewerInfo, buttonSewerEditCancel;
    private ImageView imageViewSewerEditBackButton;
    private AutoCompleteTextView autoCompleteTextViewSewerCategory;
    private Sewer sewer;
    private Intent intent;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sewer_info);
        initialComponent();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initialComponent() {
        //initialize components
        intent = getIntent();
        sewer = (Sewer) intent.getSerializableExtra("Sewer");
        editTextSewerName = findViewById(R.id.editTextSewerName);
        editTextSewerDesc = findViewById(R.id.editTextSewerDesc);
        editTextSewerLocation = findViewById(R.id.editTextSewerLocation);
        editTextSewerChannel = findViewById(R.id.editTextSewerChannel);
        editTextSewerName.setText(sewer.getName());
        editTextSewerDesc.setText(sewer.getDesc());
        editTextSewerLocation.setText(sewer.getLocation());
        editTextSewerChannel.setText(sewer.getChannel());

        imageViewSewerEditBackButton = findViewById(R.id.imageViewSewerEditBackButton);
        buttonSewerEditCancel = findViewById(R.id.buttonSewerEditCancel);
        autoCompleteTextViewSewerCategory = findViewById(R.id.my_spinner_dropdown_sewer_category);
        sewer = (Sewer) getIntent().getSerializableExtra("Sewer");
        buttonUpdateSewerInfo = findViewById(R.id.buttonUpdateSewerInfo);
        List<String> categories = new ArrayList<String>();
        categories.add("Rất nhỏ");categories.add("Nhỏ");categories.add("Vừa");categories.add("Tương đối lớn");categories.add("Lớn");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextViewSewerCategory.setAdapter(dataAdapter);
        autoCompleteTextViewSewerCategory.setText(sewer.getCategory(), false);
        buttonSewerEditCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SewerInfoActivity.this.finish();
            }
        });
        imageViewSewerEditBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SewerInfoActivity.this.finish();
            }
        });
    }

}