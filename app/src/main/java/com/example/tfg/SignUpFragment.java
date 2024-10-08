package com.example.tfg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tfg.databinding.FragmentSignUpBinding;
import com.example.tfg.utils.ValidarFormularios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    /*
        === SIGN UP FRAGMENT ===
        Esta clase se encarga del registro de nuevos usuarios
     */
    
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    MainActivity activity;
    FragmentSignUpBinding binding;

    EditText nombre, apellido, telefono, correo, contrasena, confirmarContrasena;
    
    private boolean isUploading = false;
    

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instancias
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        activity = (MainActivity) getActivity(); 

        // onClicks
        binding.botonRegistro.setOnClickListener(this);
        binding.botonIrLogin.setOnClickListener(this);
        binding.fieldContrasena.setOnFocusChangeListener(this);

        nombre = binding.fieldNombre;
        apellido = binding.fieldApellido;
        telefono = binding.fieldTelefono;
        correo = binding.fieldCorreo;
        contrasena = binding.fieldContrasena;
        confirmarContrasena = binding.fieldConfirmarContrasena;
    }

    @Override
    public void onStart(){
        super.onStart();

        // Se comprueba si el usuario ya esta logeado y se le pasa a la siguiente pantalla
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess();
        }
    }

    // Metodo para registrar a usuarios
    private void registro(){
        // Si la validacion falla no se hace nada
        if(!validarCampos()){
            return;
        }

        // Se desactiva el boton para evitar doble click
        binding.botonRegistro.setClickable(false);

        // Se guardan los datos en variables
        String cadenaNombre = nombre.getText().toString();
        String cadenaApellido = apellido.getText().toString();
        int numTelefono = Integer.parseInt(telefono.getText().toString());
        String cadenaCorreo = correo.getText().toString();
        String cadenaContrasena = contrasena.getText().toString();

        // Se crea el usuario en FirebaseAuth
        mAuth.createUserWithEmailAndPassword(cadenaCorreo, cadenaContrasena).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // Si sale bien se crea el documento del usuario
                    writeNewUser(cadenaNombre, cadenaApellido, numTelefono, cadenaCorreo);
                    onAuthSuccess();
                }else{
                    Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Metodo para validar los campos
    private boolean validarCampos(){
        // Objeto ValidarFormularios y llamada al metodo
        ValidarFormularios validarFormularios = new ValidarFormularios();
        
        return validarFormularios.validarRegistro(
                nombre,
                apellido,
                telefono,
                correo,
                contrasena,
                confirmarContrasena
        );
    }

    // Metodo para crear el documento del nuevo usuario
    private void writeNewUser(String nombre, String apellido, int telefono, String email){
        // Guardar los datos del usuario
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", nombre);
        user.put("nombre_min", nombre.toLowerCase());
        user.put("apellido", apellido);
        user.put("telefono", telefono);
        user.put("email", email);
        
        String userId = mAuth.getCurrentUser().getUid();

        // Se escribe el documento
        db.collection("usuarios").document(userId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Usuario creado con exito", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Metodo para pasar a la siguiente pantalla
    private void onAuthSuccess(){
        activity.goToFragment(new PostsFragment(), R.id.postsfragment);
    }

    // Metodo para desactivar el boton para evitar dobleClick
    private void disableButtonForDelay(long delayMillis){
        binding.botonRegistro.setEnabled(false);
        binding.botonRegistro.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.botonRegistro.setEnabled(true);
                isUploading = false;
            }
        }, delayMillis);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        if(i == R.id.botonRegistro){
            if(!isUploading){
                isUploading = true;
                disableButtonForDelay(1000);
                registro();    
            }
        }
        
        if(i == R.id.botonIrLogin){
            if(activity != null){
                activity.goToFragment(new SignInFragment(), R.id.signinfragment);
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(b){
            binding.passwordInfo.setVisibility(View.VISIBLE);
        }else{
            binding.passwordInfo.setVisibility(View.GONE);
        }
    }
}