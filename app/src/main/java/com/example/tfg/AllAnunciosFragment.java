package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tfg.databinding.FragmentAllAnunciosBinding;
import com.example.tfg.models.Anuncio;
import com.example.tfg.utils.adapters.AnuncioAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class AllAnunciosFragment extends Fragment implements View.OnClickListener {

    /*
        === ALL ANUNCIOS FRAGMENT ===
        Esta clase se encarga de traer los datos de firebase de todos los anuncios
        y mostrarlos en el recyclerView
     */
    
    private FragmentAllAnunciosBinding binding;
    private MainActivity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private List<Anuncio> anunciosList;
    private AnuncioAdapter anuncioAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllAnunciosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instancias
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        activity = (MainActivity) requireActivity();

        /*
            - Nueva lista de anuncios
            - Se crea el adapter con el onClick
            - Se añade el adapter al recycler
         */
        anunciosList = new ArrayList<>();
        anuncioAdapter = new AnuncioAdapter(anunciosList, storage, new AnuncioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Anuncio anuncio) {
                openAnuncioDetail(anuncio);
            }
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(anuncioAdapter);
        
        binding.btnNewAnuncio.setOnClickListener(this);

        // Llamada metodo para traer los anuncios de Firebase
        anunciosFromFirebase();
    }
    
    private void anunciosFromFirebase(){
        // Se obtienen los anuncios
        db.collection("anuncios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    anunciosList.clear();
                    // Por cada documento se guarda la informacion
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String id = document.getId();
                        String userId = document.getString("userId");
                        String titulo = document.getString("titulo");
                        String descripcion = document.getString("descripcion");
                        
                        Anuncio anuncio = new Anuncio(id, userId, titulo, descripcion);

                        // Nos traemos otros datos del usuario que creo el anuncio
                        db.collection("usuarios").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String nombreAutor = documentSnapshot.getString("nombre");
                                String apellidoAutor = documentSnapshot.getString("apellido");
                                anuncio.setNombreAutor(nombreAutor);
                                anuncio.setApellidoAutor(apellidoAutor);
                                anunciosList.add(anuncio);
                                anuncioAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
    }

    // Metodo para abrir los datos del anuncio
    private void openAnuncioDetail(Anuncio anuncio){
        // Se crea un Bundle y se pasan los datos a la siguiente pantalla
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
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.btnNewAnuncio){
            if(activity != null){
                activity.goToFragment(new NewAnuncioFragment(), R.id.newanunciofragment);
            }
        }
    }
}