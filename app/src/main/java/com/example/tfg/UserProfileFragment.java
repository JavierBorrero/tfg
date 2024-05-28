package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.tfg.databinding.FragmentUserProfileBinding;
import com.example.tfg.models.Usuario;
import com.example.tfg.utils.EnviarCorreos;
import com.google.firebase.auth.FirebaseAuth;

public class UserProfileFragment extends Fragment implements View.OnClickListener {
    
    FragmentUserProfileBinding binding;
    EnviarCorreos enviarCorreos;
    FirebaseAuth auth;
    
    String nombre, apellido, email, imagePfpUrl, userId;
    int telefono;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        
        auth = FirebaseAuth.getInstance();
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        userId = auth.getCurrentUser().getUid();
        
        if(getArguments() != null){
            nombre = getArguments().getString("nombre");
            apellido = getArguments().getString("apellido");
            email = getArguments().getString("email");
            imagePfpUrl = getArguments().getString("imagePfpUrl");
            telefono = getArguments().getInt("telefono");
        }
        
        binding.profileNombre.setText(nombre);
        binding.profileApellidos.setText(apellido);
        binding.profileEmail.setText(email);
        
        if(imagePfpUrl != null){
            Glide.with(binding.profilePic.getContext()).load(imagePfpUrl).into(binding.profilePic);
        }else{
            Glide.with(binding.profilePic.getContext()).load(R.drawable.icon_person_profile).into(binding.profilePic);
        }
        
        binding.btnContactar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        enviarCorreos = new EnviarCorreos();
        
        if(i == R.id.btnContactar){
            enviarCorreos.enviarCorreoContactarOtroUsuario(getContext(), userId, email);
        }
    }
}