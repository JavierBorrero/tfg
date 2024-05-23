package com.example.tfg;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tfg.databinding.FragmentMyPostsBinding;
import com.example.tfg.models.Post;
import com.example.tfg.utils.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyPostsFragment extends Fragment {

    FragmentMyPostsBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private PostAdapter postAdapter;
    private List<Post> postList;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, storage, new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(postAdapter);

        loadMyPosts();
    }

    private void loadMyPosts() {
        String currentUserId = auth.getCurrentUser().getUid();
        db.collection("posts")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            postList.clear();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String userId = document.getString("userId");
                                String titulo = document.getString("titulo");
                                String descripcion = document.getString("descripcion");
                                String localizacion = document.getString("localizacion");
                                Date fecha = document.getDate("fecha");
                                int numeroPersonas = document.getLong("numeroPersonas").intValue();
                                boolean material = document.getBoolean("materialNecesario");
                                String imageUrl = document.getString("imageUrl");

                                Post post = new Post(userId, titulo, descripcion, localizacion, fecha, numeroPersonas, material, imageUrl);

                                db.collection("usuarios").document(userId).get().addOnSuccessListener(userDoc -> {
                                    String authorName = userDoc.getString("nombre");
                                    post.setNombreAutor(authorName);
                                    postList.add(post);
                                    postAdapter.notifyDataSetChanged();
                                });
                            }
                        }
                    }
                });
    }
}