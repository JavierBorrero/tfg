package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tfg.databinding.FragmentAllPostsBinding;
import com.example.tfg.models.Post;
import com.example.tfg.utils.adapters.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        postAdapter = new PostAdapter(postList, storage, new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                openPostDetail(post);
            }
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(postAdapter);

        binding.btnNewPost.setOnClickListener(this);
        binding.btnTest.setOnClickListener(this);
        
        postsFromFirebase();
    }

    private void postsFromFirebase(){
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    postList.clear();
                    Date today = new Date();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String id = document.getId();
                        String userId = document.getString("userId");
                        String titulo = document.getString("titulo");
                        String descripcion = document.getString("descripcion");
                        String localizacion = document.getString("localizacion");
                        Date fecha = document.getDate("fecha");
                        int numeroPersonas = document.getLong("numeroPersonas").intValue();
                        boolean material = document.getBoolean("materialNecesario");
                        String imageUrl = document.getString("imageUrl");
                        
                        if(fecha != null && !fecha.before(today)){
                            Post post = new Post(id, userId, titulo, descripcion, localizacion, fecha, numeroPersonas, material, imageUrl);

                            db.collection("usuarios").document(userId).get().addOnSuccessListener(userDoc -> {
                                String nombreAutor = userDoc.getString("nombre");
                                String apellidoAutor = userDoc.getString("apellido");
                                post.setNombreAutor(nombreAutor);
                                post.setApellidoAutor(apellidoAutor);
                                postList.add(post);
                                postAdapter.notifyDataSetChanged();
                            });
                        }
                    }
                    
                }
            }
        });
    }
    
    private void intentoUpdatePostUsuarioDistinto(){
        String titulo = "nuevo titulo de Post";
        Map<String, Object> postUpdate = new HashMap<>();
        postUpdate.put("titulo", titulo);
        
        db.collection("posts").document("CUHoxPBHQ48Gw6MV4h9h")
                .update(postUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Correcto", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void openPostDetail(Post post){
        Bundle bundle = new Bundle();
        bundle.putString("id", post.getId());
        bundle.putString("userId", post.getUserId());
        bundle.putString("titulo", post.getTitulo());
        bundle.putString("descripcion", post.getDescripcion());
        bundle.putString("localizacion", post.getLocalizacion());
        bundle.putLong("fecha", post.getFechaHora().getTime());
        bundle.putInt("numeroPersonas", post.getNumeroPersonas());
        bundle.putBoolean("materialNecesario", post.isMaterial());
        bundle.putString("nombreAutor", post.getNombreAutor());
        bundle.putString("apellidoAutor", post.getApellidoAutor());
        bundle.putString("imageUrl", post.getImageUrl());
        
        PostDetailFragment postDetailFragment = new PostDetailFragment();
        postDetailFragment.setArguments(bundle);
        
        if(activity != null){
            activity.goToFragment(postDetailFragment, R.id.postdetailfragment);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.btnNewPost){
            if(activity != null){
                activity.goToFragment(new NewPostFragment(), R.id.newpostfragment);    
            }
        }
        
        if(i == R.id.btnTest){
            intentoUpdatePostUsuarioDistinto();
        }
    }
}