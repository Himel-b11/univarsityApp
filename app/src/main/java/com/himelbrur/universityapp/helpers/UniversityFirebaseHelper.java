package com.himelbrur.universityapp.helpers;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.himelbrur.universityapp.R;
import com.himelbrur.universityapp.adapters.UniversityListAdapter;
import com.himelbrur.universityapp.models.UniversityModel;

import java.util.ArrayList;

public class UniversityFirebaseHelper {
    // Field names
    public static final String NAME = "name";
    public static final String LOGO = "logo";
    public static final String ADDRESS_DISTRICT = "addressDistrict";
    public static final String TYPE = "type";
    public static final String KEY = "key";
    public static final String DETAILS = "details";

    public UniversityModel universityModel;
    public ArrayList<UniversityModel> universityModels;
    public UniversityListAdapter adapter;

    public static final String FIREBASE_DATABASE_URL = "https://university-6a38c-default-rtdb.firebaseio.com/";

    public static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(FIREBASE_DATABASE_URL).child("universities");

    public ChildEventListener childEventListener;

    AppCompatActivity activity;
    LinearLayout emptyLinerLayoutView;

    public UniversityFirebaseHelper() {}

    public void save() {
        databaseReference.child(universityModel.key).child(NAME).getRef().setValue(universityModel.name);
        databaseReference.child(universityModel.key).child(LOGO).getRef().setValue(universityModel.logo);
        databaseReference.child(universityModel.key).child(ADDRESS_DISTRICT).getRef().setValue(universityModel.addressDistrict);
        databaseReference.child(universityModel.key).child(TYPE).getRef().setValue(universityModel.type);
        databaseReference.child(universityModel.key).child(DETAILS).getRef().setValue(universityModel.details);
    }

    public void save(String key) {
        this.universityModel.key = key;
        databaseReference.child(key).child(NAME).getRef().setValue(universityModel.name);
        databaseReference.child(key).child(LOGO).getRef().setValue(universityModel.logo);
        databaseReference.child(key).child(ADDRESS_DISTRICT).getRef().setValue(universityModel.addressDistrict);
        databaseReference.child(key).child(TYPE).getRef().setValue(universityModel.type);
        databaseReference.child(key).child(DETAILS).getRef().setValue(universityModel.details);
    }

    public void setEmptyIcon(int _s) {
        if (_s < 1) {
            emptyLinerLayoutView.setVisibility(ImageView.VISIBLE);
        } else {
            emptyLinerLayoutView.setVisibility(ImageView.INVISIBLE);
        }
    }

    public ArrayList<UniversityModel> listenAndGetAll(AppCompatActivity _activity, ArrayList<UniversityModel> _universityModels, UniversityListAdapter _adapter) {
        this.adapter = _adapter;
        this.activity = _activity;
        this.universityModels = _universityModels;

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                add(snapshot);
                setEmptyIcon(1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (previousChildName == null) {
                    universityModels.set(0, buildFrom(snapshot));
                    adapter.notifyItemChanged(0);
                    return;
                }

                int i = 0;
                for (UniversityModel universityModel : universityModels) {
                    if (universityModel.key.equals(previousChildName)) {
                        universityModels.set(i + 1, buildFrom(snapshot));
                        break;
                    }
                    i++;
                }
                adapter.notifyItemChanged(i + 1);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (UniversityModel universityModel : universityModels) {
                    if (universityModel.key.equals(snapshot.getKey())) {
                        universityModels.remove(i);
                        break;
                    }
                    i++;
                }
                setEmptyIcon(universityModels.size());
                adapter.notifyItemRemoved(i);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                universityModels.clear();

                emptyLinerLayoutView = activity.findViewById(R.id.ivEmpty);
                ProgressBar pg = activity.findViewById(R.id.pg);
                pg.setVisibility(ProgressBar.INVISIBLE);

                databaseReference.removeEventListener(this);
                databaseReference.addChildEventListener(childEventListener);

                setEmptyIcon((int) snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return universityModels;
    }

    public void delete() {
        databaseReference.child(universityModel.key).getRef().removeValue();
    }

    public void add(DataSnapshot dataSnapshot) {
        UniversityModel data = UniversityFirebaseHelper.buildFrom(dataSnapshot);
        universityModels.add(data);
        universityModel = data;
        adapter.notifyItemInserted(universityModels.size() - 1);
    }

    public static UniversityModel buildFrom(DataSnapshot dataSnapshot) {
        UniversityModel universityModel = new UniversityModel();

        universityModel.key = dataSnapshot.getKey();
        universityModel.name = dataSnapshot.child(NAME).getValue(String.class);
        universityModel.logo = dataSnapshot.child(LOGO).getValue(String.class);
        universityModel.addressDistrict = dataSnapshot.child(ADDRESS_DISTRICT).getValue(String.class);
        universityModel.type = dataSnapshot.child(TYPE).getValue(String.class);
        universityModel.details = dataSnapshot.child(DETAILS).getValue(String.class);

        return universityModel;
    }

    public void removeListeners() {
        databaseReference.removeEventListener(childEventListener);
    }
}
