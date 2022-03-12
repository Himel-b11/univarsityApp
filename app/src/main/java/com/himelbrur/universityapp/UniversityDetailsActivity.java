package com.himelbrur.universityapp;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.himelbrur.universityapp.adapters.UniversityListAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.himelbrur.universityapp.helpers.UniversityFirebaseHelper;
import com.himelbrur.universityapp.models.UniversityModel;

import java.util.concurrent.atomic.AtomicReference;

public class UniversityDetailsActivity extends AppCompatActivity {
    String name;
    String details;
    String type;
    String logo;
    String addressDistrict;
    String key;

    ImageView ivUniversityLogo;
    TextView tvUniversityName;
    TextView tvUniversityType;
    TextView tvUniversityAddressDistrict;
    TextView tvUniversityDetails;

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            UniversityModel data = UniversityFirebaseHelper.buildFrom(snapshot);

            name = data.name;
            details = data.details;
            type = data.type;
            logo = data.logo;
            addressDistrict = data.addressDistrict;
            key = snapshot.getKey();


            AtomicReference<Bitmap> image = new AtomicReference<>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    image.set(UniversityListAdapter.getImage(logo));
                    ivUniversityLogo.post(new Runnable() {
                        @Override
                        public void run() {
                            if (image.get() != null) {
                                ivUniversityLogo.setImageBitmap(image.get());
                            } else {
                                ivUniversityLogo.setImageResource(R.drawable.ic_round_school_24);
                            }
                        }
                    });
                }
            }).start();

            tvUniversityName.setText(name);
            tvUniversityType.setText(type);
            tvUniversityAddressDistrict.setText(addressDistrict);
            tvUniversityDetails.setText(details);

            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_details);

        setTitle("Details");

        name = getIntent().getStringExtra(UniversityFirebaseHelper.NAME);
        details = getIntent().getStringExtra(UniversityFirebaseHelper.DETAILS);
        type = getIntent().getStringExtra(UniversityFirebaseHelper.TYPE);
        logo = getIntent().getStringExtra(UniversityFirebaseHelper.LOGO);
        addressDistrict = getIntent().getStringExtra(UniversityFirebaseHelper.ADDRESS_DISTRICT);
        key = getIntent().getStringExtra(UniversityFirebaseHelper.KEY);

        ivUniversityLogo = findViewById(R.id.ivUniversityLogo);
        tvUniversityName = findViewById(R.id.tvUniversityName);
        tvUniversityType = findViewById(R.id.tvUniversityType);
        tvUniversityAddressDistrict = findViewById(R.id.tvUniversityAddressDistrict);
        tvUniversityDetails = findViewById(R.id.tvUniversityDetails);

        Button btnUniversityDelete = findViewById(R.id.btnUniversityDelete);
        Button btnUniversityEdit = findViewById(R.id.btnUniversityEdit);

        tvUniversityName.setText(name);
        tvUniversityType.setText(type);
        tvUniversityAddressDistrict.setText(addressDistrict);
        tvUniversityDetails.setText(details);

        btnUniversityEdit.setOnClickListener(view -> {
            Intent intent = new Intent(this, EditUniversityActivity.class);
            intent.putExtra(UniversityFirebaseHelper.KEY, key);
            startActivity(intent);
        });

        databaseReference = UniversityFirebaseHelper.databaseReference.child(key);

        databaseReference.addValueEventListener(valueEventListener);

        btnUniversityDelete.setOnClickListener(view -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Are you sure to delete " + name + "?");
            alertDialog.setCancelable(true);

            alertDialog.setPositiveButton("Yes", (dialogInterface, i) -> {
                dialogInterface.cancel();
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Deleting " + name);
                progressDialog.show();
                UniversityFirebaseHelper.databaseReference.child(key).removeValue();
                progressDialog.dismiss();
                finish();
            });

            alertDialog.setNegativeButton("No", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });

            alertDialog.show();
        });

    }

    @Override
    protected void onDestroy() {
        databaseReference.removeEventListener(valueEventListener);
        super.onDestroy();
    }
}