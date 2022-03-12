package com.himelbrur.universityapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.himelbrur.universityapp.adapters.UniversityListAdapter;
import com.himelbrur.universityapp.helpers.UniversityFirebaseHelper;
import com.himelbrur.universityapp.models.UniversityModel;

import java.io.IOException;

public class EditUniversityActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progressDialog;
    String key;

    EditText etUniversityName;
    EditText etUniversityLogo;
    EditText etUniversityAddressDistrict;
    EditText etUniversityType;
    EditText etUniversityDetails;

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            UniversityModel data = UniversityFirebaseHelper.buildFrom(snapshot);
            etUniversityName.setText(data.name);
            etUniversityLogo.setText(data.logo);
            etUniversityAddressDistrict.setText(data.addressDistrict);
            etUniversityType.setText(data.type);
            etUniversityDetails.setText(data.details);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_university);

        setTitle("Edit");

        Button btnEditUniversity = findViewById(R.id.addUniversityBtn);

        btnEditUniversity.setText("Save");

        etUniversityName = findViewById(R.id.etUniversityName);
        etUniversityLogo = findViewById(R.id.etUniversityLogo);
        etUniversityAddressDistrict = findViewById(R.id.etUniversityAddressDistrict);
        etUniversityType = findViewById(R.id.etUniversityType);
        etUniversityDetails = findViewById(R.id.etUniversityDetails);

        key = getIntent().getStringExtra(UniversityFirebaseHelper.KEY);

        databaseReference = UniversityFirebaseHelper.databaseReference.child(key);
        databaseReference.addValueEventListener(valueEventListener);

        btnEditUniversity.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String universityName = etUniversityName.getText().toString();
        String universityLogo = etUniversityLogo.getText().toString();
        String universityAddressDistrict = etUniversityAddressDistrict.getText().toString();
        String universityType = etUniversityType.getText().toString();
        String universityDetails = etUniversityDetails.getText().toString();

        if (checkAllFields()) {
            showProgressDialog();

            UniversityModel universityModel = new UniversityModel();
            universityModel.key = key;
            universityModel.name = universityName;
            universityModel.logo = universityLogo;
            universityModel.addressDistrict = universityAddressDistrict;
            universityModel.type = universityType;
            universityModel.details = universityDetails;

            UniversityFirebaseHelper universityFirebaseHelper = new UniversityFirebaseHelper();
            universityFirebaseHelper.universityModel = universityModel;

            universityFirebaseHelper.save();
            progressDialog.dismiss();
            finish();
        }
    }

    private boolean checkAllFields() {
        boolean valid = true;

        if (etUniversityName.length() == 0) {
            etUniversityName.setError("Name is required");
            valid = false;
        }

        if (etUniversityLogo.length() == 0) {
            etUniversityLogo.setError("Logo is required");
            valid = false;
        }

        if (etUniversityAddressDistrict.length() == 0) {
            etUniversityAddressDistrict.setError("District is required");
            valid = false;
        }

        if (etUniversityDetails.length() == 0) {
            etUniversityDetails.setError("District is required");
            valid = false;
        }

        if (etUniversityType.length() == 0) {
            etUniversityType.setError("Type is required");
            valid = false;
        }

        return valid;
    }
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding university");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}