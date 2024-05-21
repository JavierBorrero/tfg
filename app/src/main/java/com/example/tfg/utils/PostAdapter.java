package com.example.tfg.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.models.Post;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private List<Post> postList;
    private FirebaseStorage storage;

    public PostAdapter(List<Post> postList, FirebaseStorage storage) {
        this.postList = postList;
        this.storage = storage;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post, storage);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
