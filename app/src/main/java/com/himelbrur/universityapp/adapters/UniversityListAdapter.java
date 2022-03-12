package com.himelbrur.universityapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.himelbrur.universityapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.himelbrur.universityapp.models.UniversityModel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class UniversityListAdapter extends RecyclerView.Adapter<UniversityListAdapter.ViewHolder> {
    final Context context;
    public ArrayList<UniversityModel> universities;
    private final OnItemClickListener onItemClickListener;
    private static final LruCache<String, Bitmap> memoryCache = new LruCache<String, Bitmap>(((int) (Runtime.getRuntime().maxMemory() / 1024)) / 8) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount() / 1024;
        }
    };

    public UniversityListAdapter(Context context, ArrayList<UniversityModel> universities, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.universities = universities;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.university_item, parent, false), onItemClickListener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UniversityModel university = universities.get(position);

        holder.name.setText(university.name);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap image = getImage(university.logo);
                holder.logo.post(new Runnable() {
                    @Override
                    public void run() {
                        if (image != null) {
                            holder.logo.setImageBitmap(image);
                        } else {
                            holder.logo.setImageResource(R.drawable.ic_round_school_24);
                        }
                    }
                });
            }
        }).start();

        holder.type.setText(university.type);
    }

    public static Bitmap getImage(String url) {
        Bitmap _image = getFromCache(memoryCache, url);

        if (_image == null) {
            URL _url;
            try {
                _url = new URL(url);
                _url.openConnection().connect();
                try {
                    _image = BitmapFactory.decodeStream(_url.openStream());
                    cacheBitmap(memoryCache, url, _image);
                    return _image;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return _image;
    }

    public static void cacheBitmap(LruCache<String, Bitmap> cache, String url, Bitmap bitmap) {
        if (getFromCache(cache, url) == null && url != null && bitmap != null)
            memoryCache.put(url, bitmap);
    }

    public static Bitmap getFromCache(LruCache<String, Bitmap> cache, String url) {
        if (url == null) return null;
        return memoryCache.get(url);
    }

    @Override
    public int getItemCount() {
        return universities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        ShapeableImageView logo;
        TextView type;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.name = itemView.findViewById(R.id.tvUniversityName);
            this.logo = itemView.findViewById(R.id.ivUniversityLogo);
            this.type = itemView.findViewById(R.id.tvUniversityType);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(universities.get(getAdapterPosition()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(UniversityModel universityModel);
    }
}
