package com.example.tfg.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tfg.R;
import com.example.tfg.models.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostViewHolder extends RecyclerView.ViewHolder {

    ImageView autorPfp;
    TextView autorNombre, postTitulo;
    
    public PostViewHolder(View view){
        super(view);
        autorPfp = view.findViewById(R.id.postAuthorPhoto);
        autorNombre = view.findViewById(R.id.postAuthorName);
        postTitulo = view.findViewById(R.id.postTitle);
    }
    
    public void bind(Post post, FirebaseStorage storage){
        autorNombre.setText(post.getNombreAutor());
        postTitulo.setText(post.getTitulo());

        StorageReference pfpRef = storage.getReference().child("images/" + post.getUserId() + "/pfp/");
        final long ONE_MEGABYTE = 1024 * 1024;
        pfpRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                autorPfp.setImageBitmap(bitmap);
            }
        });
    }
}
