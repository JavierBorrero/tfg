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

public class SignUpFragment extends Fragment implements View.OnClickListener {
    
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    MainActivity activity;
    private FragmentSignUpBinding binding;

    EditText nombre, apellido, telefono, correo, contrasena, confirmarContrasena;
    

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
        db = FirebaseFirestore.getInstance();
        activity = (MainActivity) getActivity(); 
        
        binding.botonRegistro.setOnClickListener(this);
        binding.botonIrLogin.setOnClickListener(this);

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
        int numTelefono = Integer.parseInt(telefono.getText().toString());
        String cadenaCorreo = correo.getText().toString();
        String cadenaContrasena = contrasena.getText().toString();

        mAuth.createUserWithEmailAndPassword(cadenaCorreo, cadenaContrasena).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    writeNewUser(cadenaNombre, cadenaApellido, numTelefono, cadenaCorreo);
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
        
        // Comprobar que el campo telefono no esta vacio
        if(telefono.getText().toString().isEmpty()){
            telefono.setError("Telefono vacio");
            validar = false;
        }else{
            telefono.setError(null);
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
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>._]).{12,}$";

        Pattern pat = Pattern.compile(passwordRegex);

        if(password == null){
            return false;
        }

        return pat.matcher(password).matches();
    }

    private void writeNewUser(String nombre, String apellido, int telefono, String email){
        // Guardar los datos del usuario
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", nombre);
        user.put("apellido", apellido);
        user.put("telefono", telefono);
        user.put("email", email);
        
        String userId = mAuth.getCurrentUser().getUid();
        
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
        activity.goToFragment(new PostsFragment(), R.id.postsfragment);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.botonRegistro){
            registro();
        } else if (i == R.id.botonIrLogin) {
            activity.goToFragment(new SignInFragment(), R.id.signinfragment);
        }
    }
}