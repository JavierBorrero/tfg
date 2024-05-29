package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tfg.databinding.FragmentNewAnuncioBinding;
import com.example.tfg.utils.ValidarFormularios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class NewAnuncioFragment extends Fragment implements View.OnClickListener {
    
    FragmentNewAnuncioBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db;
    MainActivity activity;
    
    String userId;
    boolean isUploading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewAnuncioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        activity = (MainActivity) getActivity();
        
        userId = auth.getCurrentUser().getUid();
        
        binding.btnSubmitAnuncio.setOnClickListener(this);
    }
    
    private boolean validarCampos(){
        ValidarFormularios validarFormularios = new ValidarFormularios();
        
        return validarFormularios.validarNuevoYEditarAnuncio(
                binding.fieldTitulo,
                binding.fieldDescripcion
        );
    }
    
    private void publicarAnuncio(){
        if(!validarCampos()){
            return;
        }
        
        binding.btnSubmitAnuncio.setClickable(false);
        
        String cadenaTitulo = binding.fieldTitulo.getText().toString().trim();
        String cadenaDescripcion = binding.fieldDescripcion.getText().toString().trim();

        Map<String, Object> anuncio = new HashMap<>();
        anuncio.put("userId", userId);
        anuncio.put("titulo", cadenaTitulo);
        anuncio.put("descripcion", cadenaDescripcion);
        
        db.collection("anuncios").document()
                .set(anuncio)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Anuncio publicado", Toast.LENGTH_SHORT).show();
                        activity.goToFragment(new AnunciosFragment(), R.id.anunciosfragment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void disableButtonForDelay(long delayMillis){
        binding.btnSubmitAnuncio.setEnabled(false);
        binding.btnSubmitAnuncio.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.btnSubmitAnuncio.setEnabled(true);
                isUploading = false;
            }
        }, delayMillis);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        if(i == R.id.btnSubmitAnuncio){
            if(!isUploading){
                isUploading = true;
                disableButtonForDelay(1000);
                publicarAnuncio();
            }
        }
    }
}