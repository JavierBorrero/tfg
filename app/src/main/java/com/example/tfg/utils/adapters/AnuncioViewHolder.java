package com.example.tfg.utils.adapters;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tfg.R;
import com.example.tfg.models.Anuncio;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AnuncioViewHolder extends RecyclerView.ViewHolder {
    
    ImageView autorPfp;
    TextView autorNombre, anuncioTitulo;
    
    public AnuncioViewHolder(View view){
        super(view);
        autorPfp = view.findViewById(R.id.postAuthorPhoto);
        autorNombre = view.findViewById(R.id.postAuthorName);
        anuncioTitulo = view.findViewById(R.id.postTitle);
    }
    
    public void bind(Anuncio anuncio, FirebaseStorage storage, AnuncioAdapter.OnItemClickListener listener){
        autorNombre.setText(anuncio.getNombreAutor());
        anuncioTitulo.setText(anuncio.getTitulo());

        StorageReference pfpRef = storage.getReference().child("images/"+anuncio.getUserId()+"/pfp/");
        pfpRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(autorPfp.getContext()).load(uri).into(autorPfp);
            }
        });
        
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(anuncio);
            }
        });
    }
}
