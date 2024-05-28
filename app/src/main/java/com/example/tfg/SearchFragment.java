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

import com.example.tfg.databinding.FragmentSearchBinding;
import com.example.tfg.models.Post;
import com.example.tfg.models.Usuario;
import com.example.tfg.utils.UserAdapter;
import com.example.tfg.utils.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFragment extends Fragment implements View.OnClickListener {
    
    FragmentSearchBinding binding;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private List<Post> postList;
    private List<Usuario> userList;
    private Set<String> usersIds;
    private UserAdapter userAdapter;
    private PostAdapter postAdapter;
    private boolean isSearching = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        userList = new ArrayList<>();
        postList = new ArrayList<>();
        usersIds = new HashSet<>();
        
        // Personas Recycler
        binding.recyclerUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(userList, null, null, new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Usuario usuario) {
                
            }
        });
        binding.recyclerUsuarios.setAdapter(userAdapter);
        
        // Posts Recycler
        binding.recyclerPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(postList, storage, new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                
            }
        });
        binding.recyclerPosts.setAdapter(postAdapter);
        
        binding.btnBuscar.setOnClickListener(this);
    }
    
    private void busqueda(){
        String query = binding.buscador.getText().toString().trim();
        if(!query.isEmpty()){
            realizarBusqueda(query);
        }else{
            Toast.makeText(getContext(), "Introduzca algo para buscar", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void realizarBusqueda(String query){
        binding.tituloUsuarios.setVisibility(View.VISIBLE);
        binding.tituloPosts.setVisibility(View.VISIBLE);
        buscarEnColeccionUsuarios(query);
    }
    
    private void buscarEnColeccionUsuarios(String query){
        db.collection("usuarios")
                .whereEqualTo("nombre", query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            userList.clear();
                            usersIds.clear();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Usuario usuario = document.toObject(Usuario.class);
                                userList.add(usuario);
                                usersIds.add(document.getId());
                            }
                            userAdapter.notifyDataSetChanged();
                            buscarPostsPorUserId();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void buscarPostsPorUserId(){
        postList.clear();
        for(String userId : usersIds){
            db.collection("posts")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
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

                                    db.collection("usuarios").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String nombreAutor = documentSnapshot.getString("nombre");
                                            post.setNombreAutor(nombreAutor);
                                            postList.add(post);
                                            postAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });            
        }
    }
    
    private void disableButtonForDelay(long delayMillis){
        binding.btnBuscar.setEnabled(false);
        binding.btnBuscar.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.btnBuscar.setEnabled(true);
                isSearching = false;
            }
        }, delayMillis);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.btnBuscar){
            if(!isSearching){
                isSearching = true;
                disableButtonForDelay(2000);
                busqueda();
            }
        }
    }
}