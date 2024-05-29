package com.example.tfg.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.models.Anuncio;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioViewHolder> {
    
    private List<Anuncio> anunciosList;
    private FirebaseStorage storage;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Anuncio anuncio);
    }

    public AnuncioAdapter(List<Anuncio> anunciosList, FirebaseStorage storage, OnItemClickListener onItemClickListener){
        this.anunciosList = anunciosList;
        this.storage = storage;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new AnuncioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnuncioViewHolder holder, int position) {
        Anuncio anuncio = anunciosList.get(position);
        holder.bind(anuncio, storage, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return anunciosList.size();
    }
}
