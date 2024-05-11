package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tfg.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    
    private FirebaseAuth mAuth;
    private FragmentSignUpBinding binding;

    EditText nombre, apellido, correo, contrasena, confirmarContrasena;
    
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        // Click listeners
        binding.botonRegistro.setOnClickListener(this);
        binding.botonIrLogin.setOnClickListener(this);

        nombre = binding.fieldNombre;
        apellido = binding.fieldApellido;
        correo = binding.fieldCorreo;
        contrasena = binding.fieldContrasena;
        confirmarContrasena = binding.fieldConfirmarContrasena;
    }

    @Override
    public void onStart(){
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void registro(){
        if(!validarCampos()){
            return;
        }

        String cadenaNombre = nombre.getText().toString();
        String cadenaApellido = apellido.getText().toString();
        String cadenaCorreo = correo.getText().toString();
        String cadenaContrasena = contrasena.getText().toString();

        mAuth.createUserWithEmailAndPassword(cadenaCorreo, cadenaContrasena).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    writeNewUser(cadenaNombre, cadenaApellido, cadenaCorreo);
                    onAuthSuccess(task.getResult().getUser());
                }else{
                    Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validarCampos(){
        /*
            Booleano para manejar la validacion.
            Si algun campo esta mal este valor cambia a false y la validacion
            no es correcta
         */

        boolean validar = true;

        // Comprobar que el campo nombre no esta vacio
        if(nombre.getText().toString().isEmpty()){
            nombre.setError("Campo vacio");
            validar = false;
        }else{
            nombre.setError(null);
        }

        // Comprobar que el campo apellido no esta vacio
        if(apellido.getText().toString().isEmpty()){
            apellido.setError("Campo vacio");
            validar = false;
        }else{
            apellido.setError(null);
        }

        // Comprobar que el campo email no esta vacio
        if(correo.getText().toString().isEmpty()){
            correo.setError("Campo vacio");
            validar = false;
            // Si tiene contenido se valida
        } else if (!validarEmail(correo.getText().toString())) {
            correo.setError("Email no valido");
            validar = false;
        }else{
            correo.setError(null);
        }

        // Comprobar que el campo contraseña no esta vacio
        if(contrasena.getText().toString().isEmpty()){
            contrasena.setError("Campo vacio");
            validar = false;
            // Si tiene contenido se valida
        } else if (!validarPassword(contrasena.getText().toString())) {
            contrasena.setError("Contraseña no valida");
            validar = false;
        }else{
            contrasena.setError(null);
        }

        // Comprobar que el campo confirmar contraseña no este vacio
        if(confirmarContrasena.getText().toString().isEmpty()){
            confirmarContrasena.setError("Campo vacio");
            validar = false;
        }else if(!contrasena.getText().toString().equals(confirmarContrasena.getText().toString())){
            confirmarContrasena.setError("Las contraseñas no coinciden");
            validar = false;
        }else{
            confirmarContrasena.setError(null);
        }

        return validar;
    }

    /*
        --- FUNCION VALIDAR EMAIL ---
        
        Esta funcion valida que el email tenga un formato correcto
        La cadena para validar el email es una expresion regular de 
        OWASP Validation Regex Repository
     */
    private static boolean validarEmail(String email){

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);

        if(email == null){
            return false;
        }

        return pat.matcher(email).matches();
    }

    /*
        --- FUNCION VALIDAR PASSWORD ---
        Esta funcion valida que la contraseña tenga un formato correcto
        
        Contraseña de 12 caracteres minimo que require:
        - Letra mayuscula
        - Letra minuscula
        - Numeros
        - Caracteres especiales
     */
    private static boolean validarPassword(String password){
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!¡?¿*()])[A-Za-z\\d@#$%^&+=!¡?¿*()]{12,}$\n";

        Pattern pat = Pattern.compile(passwordRegex);

        if(password == null){
            return false;
        }

        return pat.matcher(password).matches();
    }

    private void writeNewUser(String nombre, String apellido, String email){
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", nombre);
        user.put("apellido", apellido);
        user.put("email", email);
        
        int atIndex = email.indexOf('@');
        String documentName = email.substring(0, atIndex);
        
        db.collection("usuarios").document(documentName)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Creado con exito", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        /*db.collection("usuarios")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "email: " + email, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

    private void onAuthSuccess(FirebaseUser user){
        NavHostFragment.findNavController(this).navigate(R.id.postsfragment);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.botonRegistro){
            registro();
        } else if (i == R.id.botonIrLogin) {
            NavHostFragment.findNavController(this).navigate(R.id.signinfragment);
        }
    }
}