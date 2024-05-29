package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tfg.databinding.FragmentAnuncioDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AnuncioDetailFragment extends Fragment implements View.OnClickListener {
    
    FragmentAnuncioDetailBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db;
    MainActivity activity;
    
    String anuncioId, anuncioUserId, userId, titulo, descripcion, nombreAutor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnuncioDetailBinding.inflate(inflater, container, false);
        
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        activity = (MainActivity) getActivity();
        
        if(getArguments() != null){
            anuncioId = getArguments().getString("id");
            anuncioUserId = getArguments().getString("userId");
            userId = auth.getCurrentUser().getUid();
        }
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        if(getArguments() != null){
            anuncioId = getArguments().getString("id");
            anuncioUserId = getArguments().getString("userId");
            titulo = getArguments().getString("titulo");
            descripcion = getArguments().getString("descripcion");
            nombreAutor = getArguments().getString("nombreAutor");
        }
        
        userId = auth.getCurrentUser().getUid();
        if(userId.equals(anuncioUserId)){
            binding.btnContactar.setVisibility(View.GONE);
            binding.btnEditarAnuncio.setVisibility(View.VISIBLE);
            binding.btnEliminarAnuncio.setVisibility(View.VISIBLE);
        }else{
            binding.btnContactar.setVisibility(View.VISIBLE);
            binding.btnEditarAnuncio.setVisibility(View.GONE);
            binding.btnEliminarAnuncio.setVisibility(View.GONE);
        }
        
        binding.detailTitulo.setText(titulo);
        binding.detailAutor.setText(nombreAutor);
        binding.detailDescripcion.setText(descripcion);
        
        binding.btnContactar.setOnClickListener(this);
        binding.btnEditarAnuncio.setOnClickListener(this);
        binding.btnEliminarAnuncio.setOnClickListener(this);
    }
    
    private void openEditAnuncio(){
        Bundle bundle = new Bundle();
        bundle.putString("id", anuncioId);
        bundle.putString("userId", anuncioUserId);
        bundle.putString("titulo", titulo);
        bundle.putString("descripcion", descripcion);
        
        EditAnuncioFragment editAnuncioFragment = new EditAnuncioFragment();
        editAnuncioFragment.setArguments(bundle);
        
        if(activity != null){
            activity.goToFragment(editAnuncioFragment, R.id.editanunciofragment);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        if(i == R.id.btnEditarAnuncio){
            openEditAnuncio();
        }
        
        if(i == R.id.btnContactar){
            Toast.makeText(getContext(), "Boton contactar", Toast.LENGTH_SHORT).show();
        }
        
        if(i == R.id.btnEliminarAnuncio){
            Toast.makeText(getContext(), "Boton eliminar", Toast.LENGTH_SHORT).show();
        }
    }
}