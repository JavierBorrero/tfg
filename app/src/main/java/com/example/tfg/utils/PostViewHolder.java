package com.example.tfg.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
    
    public void bind(Post post, FirebaseStorage storage, PostAdapter.OnItemClickListener listener){
        autorNombre.setText(post.getNombreAutor());
        postTitulo.setText(post.getTitulo());

        StorageReference pfpRef = storage.getReference().child("images/" + post.getUserId() + "/pfp/");
        pfpRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(autorPfp.getContext()).load(uri).into(autorPfp);
            }
        });
        
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(post);
            }
        });
    }
}
