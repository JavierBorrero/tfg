package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tfg.databinding.FragmentMyPostsBinding;
import com.example.tfg.models.Post;
import com.example.tfg.utils.adapters.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyPostsFragment extends Fragment {

    /*
        === MY POSTS FRAGMENT ===
        En esta clase se ven los posts creados por el usuario registrado
     */

    FragmentMyPostsBinding binding;
    MainActivity activity;
    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth auth;
    PostAdapter postAdapter;
    List<Post> postList;
    
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

        // Instancias
        activity = (MainActivity) getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        /*
            - Nueva lista de posts
            - Se crea el adapter con el onClick
            - Se añade el adapter al recycler
         */
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, storage, new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                openPostDetail(post);
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(postAdapter);

        // Llamada al metodo
        loadMyPosts();
    }

    // Metodo para obtener los posts del usuario logeado
    private void loadMyPosts() {
        String currentUserId = auth.getCurrentUser().getUid();
        // Se obtienen los posts donde userId es el id del usuario logeado en la app
        db.collection("posts")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            postList.clear();
                            // Fecha de hoy para controlar la visibilidad de los posts
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

                                // Si el post pasa la fecha de hoy no se muestra
                                if(fecha != null && !fecha.before(today)){
                                    Post post = new Post(id, userId, titulo, descripcion, localizacion, fecha, numeroPersonas, material, imageUrl);

                                    // Se añaden algunos datos mas sobre el post
                                    db.collection("usuarios").document(userId).get().addOnSuccessListener(userDoc -> {
                                        String nombreAutor = userDoc.getString("nombre");
                                        String apellidoAutor = userDoc.getString("apellido");
                                        post.setNombreAutor(nombreAutor);
                                        post.setApellidoAutor(apellidoAutor);
                                        // Se añade a la lista
                                        postList.add(post);
                                        // Se notifica al adapter para cambiar el recycler
                                        postAdapter.notifyDataSetChanged();
                                    });
                                }
                            }
                        }
                    }
                });
    }

    // Metodo para abrir los detalles del post
    private void openPostDetail(Post post){
        // Se crea un Bundle para para los datos a la siguiente pantalla
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
}