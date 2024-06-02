package com.example.tfg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tfg.databinding.FragmentAnuncioDetailBinding;
import com.example.tfg.utils.EnviarCorreos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AnuncioDetailFragment extends Fragment implements View.OnClickListener {
    
    FragmentAnuncioDetailBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db;
    MainActivity activity;
    
    String anuncioId, anuncioUserId, userId, titulo, descripcion, nombreAutor, apellidoAutor, emailUsuarioAnuncio;

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
            apellidoAutor = getArguments().getString("apellidoAutor");
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
        binding.detailAutor.setText(String.format("%s %s", nombreAutor, apellidoAutor));
        binding.detailDescripcion.setText(descripcion);
        
        binding.btnContactar.setOnClickListener(this);
        binding.btnEditarAnuncio.setOnClickListener(this);
        binding.btnEliminarAnuncio.setOnClickListener(this);
        
        obtenerEmailUsuarioAnuncio(anuncioUserId);
    }
    
    private void obtenerEmailUsuarioAnuncio(String anuncioUserId){
        db.collection("usuarios").document(anuncioUserId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            emailUsuarioAnuncio = documentSnapshot.getString("email");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void contactarUsuarioPorAnuncio(){
        EnviarCorreos enviarCorreos = new EnviarCorreos();
        enviarCorreos.enviarCorreoContactarUsuarioAnuncio(getContext(), userId, emailUsuarioAnuncio, titulo);
    }
    
    private void eliminarAnuncio(String anuncioId){
        db.collection("anuncios").document(anuncioId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(activity != null){
                            activity.goToFragment(new AnunciosFragment(), R.id.anunciosfragment);
                        }
                        mostrarPopupEliminarAnuncio();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void mostrarPopupEliminarAnuncio(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Anuncio eliminado");
        builder.setMessage("Se ha eliminado su anuncio");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
            contactarUsuarioPorAnuncio();
        }
        
        if(i == R.id.btnEliminarAnuncio){
            eliminarAnuncio(anuncioId);
        }
    }
}