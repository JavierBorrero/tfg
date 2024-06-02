package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tfg.databinding.FragmentEditAnuncioBinding;
import com.example.tfg.utils.ValidarFormularios;
import com.example.tfg.utils.textwatchers.CustomTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class EditAnuncioFragment extends Fragment implements View.OnClickListener {
    
    FragmentEditAnuncioBinding binding;
    MainActivity activity;
    FirebaseAuth auth;
    FirebaseFirestore db;
    
    String anuncioId, usuarioId, titulo, descripcion;
    
    boolean isUploading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditAnuncioBinding.inflate(inflater, container, false);
        
        db = FirebaseFirestore.getInstance();
        activity = (MainActivity) getActivity();
        auth = FirebaseAuth.getInstance();
        
        if(getArguments() != null){
            anuncioId = getArguments().getString("id");
            usuarioId = getArguments().getString("userId");
            titulo = getArguments().getString("titulo");
            descripcion = getArguments().getString("descripcion");
        }
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        binding.editTitulo.setText(titulo);
        binding.editDescripcion.setText(descripcion);
        
        binding.btnConfirmEdit.setOnClickListener(this);
        
        addTextWatchers();
        checkForChanges();
    }
    
    private void addTextWatchers(){
        CustomTextWatcher textWatcher = new CustomTextWatcher(this::checkForChanges);
        
        binding.editTitulo.addTextChangedListener(textWatcher);
        binding.editDescripcion.addTextChangedListener(textWatcher);
    }
    
    private void checkForChanges(){
        String currentTitulo = binding.editTitulo.getText().toString().trim();
        String currentDescripcion = binding.editDescripcion.getText().toString().trim();
        
        boolean hasChanges = !currentTitulo.equals(titulo) || !currentDescripcion.equals(descripcion);
        
        binding.btnConfirmEdit.setEnabled(hasChanges);
    }
    
    private boolean validarCampos(){
        ValidarFormularios validarFormularios = new ValidarFormularios();
        
        return validarFormularios.validarNuevoYEditarAnuncio(
                binding.editTitulo,
                binding.editDescripcion
        );
    }
    
    private void aplicarEdicion(){
        if(!validarCampos()){
            return;
        }
        
        binding.btnConfirmEdit.setClickable(false);
        
        String cadenaTitulo = binding.editTitulo.getText().toString().trim();
        String cadenaDescripcion = binding.editDescripcion.getText().toString().trim();
        
        Map<String, Object> anuncio = new HashMap<>();
        anuncio.put("titulo", cadenaTitulo);
        anuncio.put("descripcion", cadenaDescripcion);
        
        db.collection("anuncios").document(anuncioId)
                .update(anuncio)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Anuncio editado", Toast.LENGTH_SHORT).show();
                        if(activity != null){
                            activity.goToFragment(new AnunciosFragment(), R.id.anunciosfragment);
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

    private void disableButtonForDelay(long delayMillis){
        binding.btnConfirmEdit.setEnabled(false);
        binding.btnConfirmEdit.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.btnConfirmEdit.setEnabled(true);
                isUploading = false;
            }
        }, delayMillis);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        if(i == R.id.btnConfirmEdit){
            if(!isUploading){
                isUploading = true;
                disableButtonForDelay(1000);
                aplicarEdicion();
            }
        }
    }
}