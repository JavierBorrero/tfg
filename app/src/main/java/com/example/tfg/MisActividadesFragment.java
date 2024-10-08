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
import com.example.tfg.utils.adapters.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MisActividadesFragment extends Fragment {

    /*
        === MIS ACTIVIDADES FRAGMENT ===
        En esta clase se ve a que actividades esta apuntado el usuario registrado
     */
    
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

        // Instancias
        activity = (MainActivity) getActivity(); 
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        /*
            - Se crea el adapter con el onClick
            - Se añade el adapter al recycler
         */
        binding.recyclerMisActividades.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(postsList, storage, new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                openPostDetail(post);
            }
        });
        binding.recyclerMisActividades.setAdapter(postAdapter);

        // Llamada al metodo
        usuarioActividades();
    }

    // Metodo para obtener las actividades a las que esta apuntado un usuario
    private void usuarioActividades(){
        String currentUserId = auth.getCurrentUser().getUid();

        // Busqueda en la coleccion de posts donde el userId este en usuariosRegistrados
        db.collection("posts")
                .whereEqualTo("usuariosRegistrados." + currentUserId, true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            postsList.clear();
                            // Se guarda la fecha de hoy para controlar que posts se muestran o no
                            Date today = new Date();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                // Por cada documento se guardan los datos
                                String id = document.getId();
                                String userId = document.getString("userId");
                                String titulo = document.getString("titulo");
                                String descripcion = document.getString("descripcion");
                                String localizacion = document.getString("localizacion");
                                Date fecha = document.getDate("fecha");
                                int numeroPersonas = document.getLong("numeroPersonas").intValue();
                                boolean material = document.getBoolean("materialNecesario");
                                String imageUrl = document.getString("imageUrl");

                                // Si el post pasa de la fecha de hoy no se muestra
                                if(fecha != null && !fecha.before(today)){
                                    Post post = new Post(id, userId, titulo, descripcion, localizacion, fecha, numeroPersonas, material, imageUrl);

                                    db.collection("usuarios").document(userId).get().addOnSuccessListener(userDoc -> {
                                        String nombreAutor = userDoc.getString("nombre");
                                        String apellidoAutor = userDoc.getString("apellido");
                                        post.setNombreAutor(nombreAutor);
                                        post.setApellidoAutor(apellidoAutor);
                                        // Se añade a la lista
                                        postsList.add(post);
                                        // Se notifica al recycler
                                        postAdapter.notifyDataSetChanged();
                                    });
                                }
                            }
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

    // Metodo para abrir los detalles del post
    private void openPostDetail(Post post){
        // Bundle para pasar los datos a la otra pantalla
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