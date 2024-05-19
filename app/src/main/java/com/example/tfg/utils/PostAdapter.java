package com.example.tfg.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.PostsFragment;
import com.example.tfg.R;
import com.example.tfg.models.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private StorageReference storageReference;
    private boolean showMyPosts;

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView authorName;
        public TextView title;
        public ImageView image;

        public PostViewHolder(View itemView) {
            super(itemView);
            authorName = itemView.findViewById(R.id.postAuthorName);
            title = itemView.findViewById(R.id.postTitle);
            image = itemView.findViewById(R.id.postAuthorPhoto);
        }
    }

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.authorName.setText(post.getNombreAutor());
        holder.title.setText(post.getTitulo());
        
        // Obtener la imagen
        StorageReference imageRef = storageReference.child("images/"+post.getUserId()+"/pfp/");
        final long megabytes = 5 * 1024 * 1024;
        imageRef.getBytes(megabytes).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.image.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
