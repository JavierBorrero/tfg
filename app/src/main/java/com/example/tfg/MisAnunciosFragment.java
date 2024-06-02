package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tfg.databinding.FragmentMisAnunciosBinding;
import com.example.tfg.models.Anuncio;
import com.example.tfg.utils.adapters.AnuncioAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class MisAnunciosFragment extends Fragment {
    
    FragmentMisAnunciosBinding binding;
    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth auth;
    AnuncioAdapter anuncioAdapter;
    List<Anuncio> anunciosList;
    private MainActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMisAnunciosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        activity = (MainActivity) getActivity(); 
        
        anunciosList = new ArrayList<>();
        anuncioAdapter = new AnuncioAdapter(anunciosList, storage, new AnuncioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Anuncio anuncio) {
                openAnuncioDetail(anuncio);
            }
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(anuncioAdapter);
        
        loadMisAnuncios();
    }
    
    private void loadMisAnuncios(){
        String currentUserId = auth.getCurrentUser().getUid();
        db.collection("anuncios")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            anunciosList.clear();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String id = document.getId();
                                String userId = document.getString("userId");
                                String titulo = document.getString("titulo");
                                String descripcion = document.getString("descripcion");
                                
                                Anuncio anuncio = new Anuncio(id, userId, titulo, descripcion);
                                
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
    
    private void openAnuncioDetail(Anuncio anuncio){
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
}