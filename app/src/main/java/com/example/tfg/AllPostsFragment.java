package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tfg.databinding.FragmentAllPostsBinding;
import com.example.tfg.models.Post;
import com.example.tfg.utils.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllPostsFragment extends Fragment implements View.OnClickListener {
    
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private List<Post> postList;
    private PostAdapter postAdapter;
    MainActivity activity;
    FragmentAllPostsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        activity = (MainActivity) requireActivity(); 

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, storage);
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(postAdapter);

        binding.btnNewPost.setOnClickListener(this);
        
        postsFromFirebase();
    }

    private void postsFromFirebase(){
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        
                        Post post = new Post(userId, titulo, descripcion, localizacion, fecha, numeroPersonas, material);
                        
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

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.btnNewPost){
            if(activity != null){
                activity.goToFragment(new NewPostFragment(), R.id.newpostfragment);    
            }
        }
    }
}