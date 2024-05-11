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

import com.example.tfg.databinding.FragmentSignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInFragment extends Fragment implements View.OnClickListener {
    
    private FirebaseAuth mAuth;
    
    private FragmentSignInBinding binding;
    
    EditText email, password;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        
        binding.btnInicioSesion.setOnClickListener(this);
        binding.btnIrRegistro.setOnClickListener(this);
        
        email = binding.fieldEmail;
        password = binding.fieldPassword;
    }

    @Override
    public void onStart(){
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void inicioSesion(){
        if(!validarCampos()){
            return;
        }

        String cadenaEmail = email.getText().toString();
        String cadenaPassword = password.getText().toString();

        mAuth.signInWithEmailAndPassword(cadenaEmail, cadenaPassword).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    onAuthSuccess(task.getResult().getUser());
                }else{
                    Toast.makeText(getContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // --- Funcion para validar los campos del log in ---
    private boolean validarCampos(){
        boolean validar = true;

        if(email.getText().toString().isEmpty()){
            email.setError("Required");
            validar = false;
        }else{
            email.setError(null);
        }

        if(password.getText().toString().isEmpty()){
            password.setError("Required");
            validar = false;
        }else{
            password.setError(null);
        }

        return validar;
    }

    private void onAuthSuccess(FirebaseUser user){
        NavHostFragment.findNavController(this).navigate(R.id.postsfragment);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.btnInicioSesion){
            inicioSesion();
        }else if (i == R.id.btnIrRegistro){
            NavHostFragment.findNavController(this).navigate(R.id.signupfragment);
        }
    }
}