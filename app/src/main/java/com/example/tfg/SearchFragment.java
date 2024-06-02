package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tfg.databinding.FragmentSearchBinding;
import com.example.tfg.models.Anuncio;
import com.example.tfg.models.Post;
import com.example.tfg.models.Usuario;
import com.example.tfg.utils.adapters.AnuncioAdapter;
import com.example.tfg.utils.adapters.UserAdapter;
import com.example.tfg.utils.adapters.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class SearchFragment extends Fragment implements UserAdapter.OnItemClickListener {

    /*
        === SEARCH FRAGMENT ===
        Esta clase se encarga de buscar entre usuarios, posts y anuncios
        Busca segun el nombre del usuario
     */
    
    FragmentSearchBinding binding;
    private MainActivity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private List<Post> postList;
    private List<Usuario> userList;
    private List<Anuncio> anuncioList;
    private UserAdapter userAdapter;
    private PostAdapter postAdapter;
    private AnuncioAdapter anuncioAdapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable busquedaRunnable;
    private String userIdAuth;

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

        // Instancias
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        activity = (MainActivity) getActivity();

        // Listas vacias
        userList = new ArrayList<>();
        postList = new ArrayList<>();
        anuncioList = new ArrayList<>();

        // UserId en variable
        userIdAuth = auth.getCurrentUser().getUid();
        
        // Personas Recycler
        binding.recyclerUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(userList, null, null, this);
        binding.recyclerUsuarios.setAdapter(userAdapter);
        
        // Posts Recycler
        binding.recyclerPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(postList, storage, new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                openPostDetail(post);
            }
        });
        binding.recyclerPosts.setAdapter(postAdapter);
        
        // Anuncios Recycler
        binding.recyclerAnuncios.setLayoutManager(new LinearLayoutManager(getContext()));
        anuncioAdapter = new AnuncioAdapter(anuncioList, storage, new AnuncioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Anuncio anuncio) {
                openAnuncioDetail(anuncio);
            }
        });
        binding.recyclerAnuncios.setAdapter(anuncioAdapter);

        // TextWatcher para que solo busque cuando se hayan escrito al menos 3 letras
        binding.buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler.removeCallbacks(busquedaRunnable);

                busquedaRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String textoBusqueda = editable.toString().trim();
                        if(textoBusqueda.length() >= 3){
                            realizarBusqueda(textoBusqueda);
                        }
                    }
                };
                handler.postDelayed(busquedaRunnable, 2000);
            }
        });
    }

    // Metodo que realiza la busqueda
    private void realizarBusqueda(String textBusqueda){
        String nombreBusqueda = textBusqueda.toLowerCase();
        String finalNombreBusqueda = nombreBusqueda + "\uf8ff";

        // Busqueda en la coleccion de usuarios
        db.collection("usuarios")
                .whereGreaterThanOrEqualTo("nombre_min", nombreBusqueda)
                .whereLessThanOrEqualTo("nombre_min", finalNombreBusqueda)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            userList.clear();
                            // Lista de ids de usuarios
                            List<String> usersIds = new ArrayList<>();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String userId = document.getId();
                                Usuario usuario = document.toObject(Usuario.class);
                                usuario.setId(userId);
                                // Se añaden los usuarios a la lista
                                userList.add(usuario);
                                // Se añaden los ids de los usuarios a la lista
                                usersIds.add(userId);
                            }
                            userAdapter.notifyDataSetChanged();
                            // Se busca en los posts y en los anuncios de los usuarios segun el userId
                            obtenerPostsDeUsuarios(usersIds);
                            obtenerAnunciosDeUsuarios(usersIds);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Se muestran segun la lista
        binding.tituloUsuarios.setVisibility(userList.isEmpty() ? View.VISIBLE : View.GONE);
        binding.viewTituloUsuarios.setVisibility(userList.isEmpty() ? View.VISIBLE : View.GONE);
        binding.tituloPosts.setVisibility(postList.isEmpty() ? View.VISIBLE : View.GONE);
        binding.viewTituloPosts.setVisibility(postList.isEmpty() ? View.VISIBLE : View.GONE);
        binding.tituloAnuncios.setVisibility(anuncioList.isEmpty() ? View.VISIBLE : View.GONE);
        binding.viewTituloAnuncios.setVisibility(anuncioList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // Metodo para obtener los posts del usuario
    private void obtenerPostsDeUsuarios(List<String> usersIds){
        // Se obtienen los posts por cada id
        for(String userId : usersIds){
            db.collection("posts")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                postList.clear();
                                // Se guarda le fecha de hoy para controlar si se muestra el post
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
                                            // Se añaden datos extras sobre el post
                                            String nombreAutor = userDoc.getString("nombre");
                                            String apellidoAutor = userDoc.getString("apellido");
                                            post.setNombreAutor(nombreAutor);
                                            post.setApellidoAutor(apellidoAutor);
                                            // Se añade el post a la lista
                                            postList.add(post);
                                            // Se notifica al adapter para cambiar el recyler
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
    }

    // Metodo para obtener los anuncios del usuario
    private void obtenerAnunciosDeUsuarios(List<String> usersIds){
        // Se obtiene los usuarios por cada id
        for(String userId : usersIds){
            db.collection("anuncios")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                anuncioList.clear();
                                for(QueryDocumentSnapshot document : task.getResult()){
                                    String id = document.getId();
                                    String userId = document.getString("userId");
                                    String titulo = document.getString("titulo");
                                    String descripcion = document.getString("descripcion");

                                    Anuncio anuncio = new Anuncio(id, userId, titulo, descripcion);

                                    db.collection("usuarios").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            // Se añade informacion extra sobre el autor
                                            String nombreAutor = documentSnapshot.getString("nombre");
                                            String apellidoAutor = documentSnapshot.getString("apellido");
                                            anuncio.setNombreAutor(nombreAutor);
                                            anuncio.setApellidoAutor(apellidoAutor);
                                            // Se añade el anuncio a la lista
                                            anuncioList.add(anuncio);
                                            // Se notifica al adapter para cambiar el recycler
                                            anuncioAdapter.notifyDataSetChanged();
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

    // Metodo para abrir los detalles del usuario
    private void openUserDetail(Usuario usuario){
        // Si es igual al usuario registrado se va a la pantalla account
        if(usuario.getId().equals(userIdAuth)){
            if(activity != null){
                activity.goToFragment(new AccountFragment(), R.id.accountfragment);
            }
        }else{
            // Se crea un Bundle y se pasan los datos a la siguiente pantalla
            Bundle bundle = new Bundle();
            bundle.putString("nombre", usuario.getNombre());
            bundle.putString("apellido", usuario.getApellido());
            bundle.putString("email", usuario.getEmail());
            bundle.putInt("telefono", usuario.getTelefono());
            bundle.putString("imagePfpUrl", usuario.getImagePfpUrl());

            UserProfileFragment userProfileFragment = new UserProfileFragment();
            userProfileFragment.setArguments(bundle);

            if(activity != null){
                activity.goToFragment(userProfileFragment, R.id.userprofilefragment);
            }
        }
    }

    // Metodo para abrir los detalles del post
    private void openPostDetail(Post post){
        // Se crea un Bundle para pasar los datos a la siguiente pantalla
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

    // Metodo para abrir los datos del anuncio
    private void openAnuncioDetail(Anuncio anuncio){
        // Se crea un Bundle para pasar los datos a la siguiente pantalla
        Bundle bundle = new Bundle();
        bundle.putString("id", anuncio.getId());
        bundle.putString("userId", anuncio.getUserId());
        bundle.putString("titulo", anuncio.getTitulo());
        bundle.putString("descripcion", anuncio.getDescripcion());
        bundle.putString("nombreAutor", anuncio.getNombreAutor());
        bundle.putString("apellidoAutor", anuncio.getApellidoAutor());

        AnuncioDetailFragment anuncioDetailFragment = new AnuncioDetailFragment();
        anuncioDetailFragment.setArguments(bundle);

        if(activity != null){
            activity.goToFragment(anuncioDetailFragment, R.id.anunciodetailfragment);
        }
    }

    @Override
    public void onItemClick(Usuario usuario) {
        openUserDetail(usuario);
    }
}