package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tfg.databinding.FragmentForgotPasswordBinding;
import com.example.tfg.utils.ValidarFormularios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {
    
    FragmentForgotPasswordBinding binding;
    FirebaseAuth auth;
    String email;
    EditText inputCorreo;
    MainActivity activity;
    
    private boolean isUploading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        activity = (MainActivity) getActivity();
        auth = FirebaseAuth.getInstance();
        
        binding.btnConfirmar.setOnClickListener(this);
        binding.volverSignIn.setOnClickListener(this);
        binding.volverSignUp.setOnClickListener(this);
        inputCorreo = binding.inputCorreoRecuperar;
        
    }

    private boolean validarCampos(){
        ValidarFormularios validarFormularios = new ValidarFormularios();
        
        return validarFormularios.validarEmailRecuperarContrasena(
                binding.inputCorreoRecuperar
        );
    }
    
    private void recuperarContrasena(){
        if(!validarCampos()){
            return;
        }
        
        binding.btnConfirmar.setClickable(false);
        
        email = inputCorreo.getText().toString().trim();
        
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    binding.textInfoCorreo.setText(R.string.correo_enviado);
                }
            }
        });
    }

    private void disableButtonForDelay(long delayMillis){
        binding.btnConfirmar.setEnabled(false);
        binding.btnConfirmar.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.btnConfirmar.setEnabled(true);
                isUploading = false;
            }
        }, delayMillis);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        if(i == R.id.btnConfirmar){
            if(!isUploading){
                isUploading = true;
                disableButtonForDelay(1000);
                recuperarContrasena();
            }
        }
        
        if(i == R.id.volverSignIn){
            if(activity != null){
                activity.goToFragment(new SignInFragment(), R.id.signinfragment);
            }
        }
        
        if(i == R.id.volverSignUp){
            if(activity != null){
                activity.goToFragment(new SignUpFragment(), R.id.signupfragment);
            }
        }
    }
}