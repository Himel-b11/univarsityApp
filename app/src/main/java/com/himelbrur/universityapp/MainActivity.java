package com.himelbrur.universityapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.himelbrur.universityapp.adapters.UniversityListAdapter;
import com.himelbrur.universityapp.helpers.UniversityFirebaseHelper;
import com.himelbrur.universityapp.models.UniversityModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements UniversityListAdapter.OnItemClickListener {
    RecyclerView recyclerView;
    UniversityFirebaseHelper universityFirebaseHelper = new UniversityFirebaseHelper();
    ArrayList<UniversityModel> universities = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Universities");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final UniversityListAdapter adapter = new UniversityListAdapter(this, universities, this);
        adapter.universities = universityFirebaseHelper.listenAndGetAll(this, universities, adapter);

        recyclerView.setAdapter(adapter);

        FloatingActionButton floatingActionButton = findViewById(R.id.fabAddUniversityBtn);
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddUniversityActivity.class);
            startActivity(intent);
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            universityFirebaseHelper.removeListeners();
            refreshView();
        });
    }

    private void refreshView() {
        universities.clear();
        final UniversityListAdapter adapter = new UniversityListAdapter(MainActivity.this, universities, this);
        adapter.universities = universityFirebaseHelper.listenAndGetAll(MainActivity.this, universities, adapter);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(UniversityModel universityModel) {
        Intent intent = new Intent(this, UniversityDetailsActivity.class);
        intent.putExtra(UniversityFirebaseHelper.NAME, universityModel.name);
        intent.putExtra(UniversityFirebaseHelper.DETAILS, universityModel.details);
        intent.putExtra(UniversityFirebaseHelper.LOGO, universityModel.logo);
        intent.putExtra(UniversityFirebaseHelper.ADDRESS_DISTRICT, universityModel.addressDistrict);
        intent.putExtra(UniversityFirebaseHelper.TYPE, universityModel.type);
        intent.putExtra(UniversityFirebaseHelper.KEY, universityModel.key);
        startActivity(intent);
    }
}