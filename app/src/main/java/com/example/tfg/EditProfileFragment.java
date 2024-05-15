package com.example.tfg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tfg.databinding.FragmentEditProfileBinding;
import com.example.tfg.models.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment implements View.OnClickListener {
    
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference storageReference;
    
    private FragmentEditProfileBinding binding;
    
    EditText nombre, apellidos, telefono;
    
    ImageView imagen;
    
    String userId, userEmail;
    
    Usuario usuario;
    
    Uri image;

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        if (result.getData() != null) {
                            image = result.getData().getData();
                            imagen.setImageURI(image);
                        }
                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        storageReference = FirebaseStorage.getInstance().getReference();
        
        nombre = binding.fieldNombre;
        apellidos = binding.fieldApellido;
        telefono = binding.fieldTelefono;
        imagen = binding.profilePic;
        
        userId = mAuth.getCurrentUser().getUid();
        
        obtenerDatosUsuario(userId);
        
        binding.btnAceptar.setOnClickListener(this);
        binding.btnCancelar.setOnClickListener(this);
        binding.btnEditPfp.setOnClickListener(this);
        
    }
    
    // === OBTENER LOS DATOS DEL USUARIO ===
    private void obtenerDatosUsuario(String usuarioId){

        DocumentReference docRef = db.collection("usuarios").document(usuarioId);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    usuario = documentSnapshot.toObject(Usuario.class);
                    nombre.setText(usuario.getNombre());
                    apellidos.setText(usuario.getApellido());
                    telefono.setText(String.valueOf(usuario.getTelefono()));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // === INICIO FUNCIONES PARA OBTENER PATH IMAGEN ===
    private void lanzarIntentObtenerImagen(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryActivityResultLauncher.launch(intent);
    }
    
    // === VALIDAR CAMPOS ===
    private boolean validarCampos(){
        boolean validar = true;

        // Validar si el campo nombre esta vacio
        if(nombre.getText().toString().isEmpty()){
            nombre.setError("Nombre vacio");
            validar = false;
        }else{
            nombre.setError(null);
        }

        if(apellidos.getText().toString().isEmpty()){
            apellidos.setError("Apellido vacio");
            validar = false;
        }else{
            apellidos.setError(null);
        }

        if(telefono.getText().toString().isEmpty()){
            telefono.setError("Telefono vacio");
            validar = false;
        }else{
            telefono.setError(null);
        }

        return validar;
    }
    
    private void aplicarCambios(){
        if(!validarCampos()){
            return;
        }
        
        String cadenaNombre = nombre.getText().toString();
        String cadenaApellido = apellidos.getText().toString();
        int numTelefono = Integer.parseInt(telefono.getText().toString());
        
        editarPerfil(userId, userEmail, cadenaNombre, cadenaApellido, numTelefono);
        editarFotoPerfil(image);
        
    }
    
    private void editarPerfil(String usuarioId, String email, String nombre, String apellido, int telefono){
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("nombre", nombre);
        user.put("apellido", apellido);
        user.put("telefono", telefono);
        
        db.collection("usuarios").document(usuarioId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Perfil editado con exito", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(EditProfileFragment.this).navigate(R.id.accountfragment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        
    }
    
    private void editarFotoPerfil(Uri file){
        
        StorageReference reference = storageReference.child("images/"+userId+"/pfp/");
        reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "todo correcto", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        
    }
    
    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        if(i == R.id.btnEditPfp){
          lanzarIntentObtenerImagen();
        }
        
        if(i == R.id.btnCancelar){
            NavHostFragment.findNavController(this).navigate(R.id.accountfragment);
        }
        
        if(i == R.id.btnAceptar){
            aplicarCambios();
        }
        
    }
}