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

import com.example.tfg.databinding.FragmentMisActividadesBinding;
import com.example.tfg.models.Post;
import com.example.tfg.utils.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MisActividadesFragment extends Fragment {
    
    FragmentMisActividadesBinding binding;
    MainActivity activity;
    FirebaseAuth auth;
    FirebaseFirestore db;
    PostAdapter postAdapter;
    List<Post> postsList = new ArrayList<>();
    FirebaseStorage storage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMisActividadesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        activity = (MainActivity) getActivity(); 
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        
        binding.recyclerMisActividades.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(postsList, storage, new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                openPostDetail(post);
            }
        });
        binding.recyclerMisActividades.setAdapter(postAdapter);
        
        usuarioActividades();
    }
    
    private void usuarioActividades(){
        String currentUserId = auth.getCurrentUser().getUid();
        
        db.collection("posts")
                .whereEqualTo("usuariosRegistrados." + currentUserId, true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            postsList.clear();
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

                                Post post = new Post(id, userId, titulo, descripcion, localizacion, fecha, numeroPersonas, material, imageUrl);

                                db.collection("usuarios").document(userId).get().addOnSuccessListener(userDoc -> {
                                    String authorName = userDoc.getString("nombre");
                                    post.setNombreAutor(authorName);
                                    postsList.add(post);
                                    postAdapter.notifyDataSetChanged();
                                    
                                    if(postsList.isEmpty()){
                                        noMostrarRecycler();
                                    }else{
                                        mostrarRecycler(postsList);
                                    }
                                });
                            }
                            Log.d("DEBUG", "POSTS LIST: " + postsList.size());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void noMostrarRecycler(){
        binding.tituloNingunaActividad.setVisibility(View.VISIBLE);
        binding.recyclerMisActividades.setVisibility(View.GONE);
    }
    
    private void mostrarRecycler(List<Post> postsList){
        binding.tituloNingunaActividad.setVisibility(View.GONE);
        binding.recyclerMisActividades.setVisibility(View.VISIBLE);
        
        binding.recyclerMisActividades.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(postsList, storage, new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                openPostDetail(post);
            }
        });
        binding.recyclerMisActividades.setAdapter(postAdapter);
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
        bundle.putString("imageUrl", post.getImageUrl());

        PostDetailFragment postDetailFragment = new PostDetailFragment();
        postDetailFragment.setArguments(bundle);

        if(activity != null){
            activity.goToFragment(postDetailFragment, R.id.postdetailfragment);
        }
    }
}