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

import com.example.tfg.databinding.FragmentSignInBinding;
import com.example.tfg.utils.ValidarFormularios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInFragment extends Fragment implements View.OnClickListener {

    /*
        === SIGN IN FRAGMENT ===
        Esta clase se encarga de iniciar sesion a los usuarios
     */
    
    private FirebaseAuth mAuth;
    
    private FragmentSignInBinding binding;
    
    EditText email, password;
    
    MainActivity activity;

    boolean isUploading = false;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instancias
        mAuth = FirebaseAuth.getInstance();
        activity = (MainActivity) getActivity();

        // onClicks
        binding.btnInicioSesion.setOnClickListener(this);
        binding.btnIrRegistro.setOnClickListener(this);
        binding.recuperarContrasena.setOnClickListener(this);

        email = binding.fieldEmail;
        password = binding.fieldPassword;
    }

    @Override
    public void onStart(){
        super.onStart();

        // Si el usuario esta logeado se le pasa a la siguiente pantalla
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess();
        }
    }

    // Metodo para iniciar sesion
    private void inicioSesion(){
        // Si la validacion falla no se hace nada
        if(!validarCampos()){
            return;
        }

        binding.btnInicioSesion.setClickable(false);

        // Se guardan los datos en variables
        String cadenaEmail = email.getText().toString();
        String cadenaPassword = password.getText().toString();

        // Se inicia sesion con FirebaseAuth
        mAuth.signInWithEmailAndPassword(cadenaEmail, cadenaPassword).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    onAuthSuccess();
                }else{
                    Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Metodo para validar los campos del login
    private boolean validarCampos(){
        // Objeto de la clase ValidarFormularios y llamada al metodo
        ValidarFormularios validarFormularios = new ValidarFormularios();
        
        return validarFormularios.validarInicioSesion(
                email, 
                password
        );
    }

    // Metodo para comprobar si el login sale bien
    private void onAuthSuccess(){
        // Si sale bien se salta de pantalla
        if(activity != null){
            activity.goToFragment(new PostsFragment(), R.id.postsfragment);
        }
    }

    // Metodo para desactivar el boton para evitar dobleClick
    private void disableButtonForDelay(long delayMillis){
        binding.btnInicioSesion.setEnabled(false);
        binding.btnInicioSesion.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.btnInicioSesion.setEnabled(true);
                isUploading = false;
            }
        }, delayMillis);
    }
    
    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        if(i == R.id.btnInicioSesion){
            if(!isUploading){
                isUploading = true;
                disableButtonForDelay(1000);
                inicioSesion();
            }
        }
        
        if(i == R.id.btnIrRegistro){
            if(activity != null){
                activity.goToFragment(new SignUpFragment(), R.id.signupfragment);
            }
        }
        
        if(i == R.id.recuperarContrasena){
            if(activity != null){
                activity.goToFragment(new ForgotPasswordFragment(), R.id.forgotpasswordfragment);
            }
        }
    }
}