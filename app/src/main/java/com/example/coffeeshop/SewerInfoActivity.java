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

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

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
        editTextSewerName.setText(sewer.getName());
        editTextSewerDesc.setText(sewer.getDescription());
        editTextSewerLocation.setText("Location: " +sewer.getLocation().get("district") + ", " + sewer.getLocation().get("city"));
        editTextSewerChannel.setText(sewer.getId());

        imageViewSewerEditBackButton = findViewById(R.id.imageViewSewerEditBackButton);
        buttonSewerEditCancel = findViewById(R.id.buttonSewerEditCancel);
        buttonUpdateSewerInfo = findViewById(R.id.buttonUpdateSewerInfo);
        List<String> categories = new ArrayList<String>();
        categories.add("Rất nhỏ");categories.add("Nhỏ");categories.add("Vừa");categories.add("Tương đối lớn");categories.add("Lớn");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextViewSewerCategory.setAdapter(dataAdapter);
//        autoCompleteTextViewSewerCategory.setText(sewer.getCategory(), false);
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