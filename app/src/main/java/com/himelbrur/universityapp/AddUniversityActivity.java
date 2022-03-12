package com.himelbrur.universityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.himelbrur.universityapp.helpers.UniversityFirebaseHelper;
import com.himelbrur.universityapp.models.UniversityModel;

import java.util.Objects;

public class AddUniversityActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    EditText etUniversityName;
    EditText etUniversityLogo;
    EditText etUniversityAddressDistrict;
    EditText etUniversityType;
    EditText etUniversityDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_add_university);

        setTitle("Add a new University");

        Button btn = findViewById(R.id.addUniversityBtn);

        etUniversityName = findViewById(R.id.etUniversityName);
        etUniversityLogo = findViewById(R.id.etUniversityLogo);
        etUniversityAddressDistrict = findViewById(R.id.etUniversityAddressDistrict);
        etUniversityType = findViewById(R.id.etUniversityType);
        etUniversityDetails = findViewById(R.id.etUniversityDetails);

        btn.setOnClickListener(new View.OnClickListener() {
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
                    universityModel.name = universityName;
                    universityModel.logo = universityLogo;
                    universityModel.addressDistrict = universityAddressDistrict;
                    universityModel.type = universityType;
                    universityModel.details = universityDetails;

                    UniversityFirebaseHelper universityFirebaseHelper = new UniversityFirebaseHelper();
                    universityFirebaseHelper.universityModel = universityModel;

                    universityFirebaseHelper.save(String.valueOf(System.currentTimeMillis()));
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
        });
    }
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding university");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}