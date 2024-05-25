package com.example.tfg;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tfg.databinding.FragmentPostDetailBinding;
import com.example.tfg.utils.EnviarCorreos;
import com.example.tfg.utils.RegistroActividad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostDetailFragment extends Fragment implements View.OnClickListener {
    
    FragmentPostDetailBinding binding;
    
    String postId, userId, postUserId, titulo, descripcion, localizacion, nombreAutor, fechaFormateada, imageUrl, keyToRemove;
    Date fecha;
    long fechaLong;
    int numeroPersonas;
    boolean materialNecesario, estaRegistrado;
    
    FirebaseAuth auth;
    FirebaseFirestore db;
    MainActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostDetailBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        if(getArguments() != null){
            postId = getArguments().getString("id");
            userId = auth.getCurrentUser().getUid();
            
            comprobarUsuarioRegistrado(postId, userId, binding.btnApuntarActividad);
            
            binding.btnApuntarActividad.setOnClickListener(this);
            
        }
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (MainActivity) getActivity(); 
        
        // Recuperar datos del Bundle
        if(getArguments() != null){
            postId = getArguments().getString("id");
            postUserId = getArguments().getString("userId");
            titulo = getArguments().getString("titulo");    
            descripcion = getArguments().getString("descripcion");
            localizacion = getArguments().getString("localizacion");
            fechaLong = getArguments().getLong("fecha");
            fecha = new Date(fechaLong);
            numeroPersonas = getArguments().getInt("numeroPersonas");
            materialNecesario = getArguments().getBoolean("materialNecesario");
            nombreAutor = getArguments().getString("nombreAutor");
            imageUrl = getArguments().getString("imageUrl");
            
            // Formatear la fecha
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            fechaFormateada = dateFormat.format(fecha);
        }
        
        String currentUser = auth.getCurrentUser().getUid();
        if(currentUser.equals(postUserId)){
            binding.btnApuntarActividad.setVisibility(View.GONE);
            binding.btnEditarPost.setVisibility(View.VISIBLE);
            binding.btnBorrarPost.setVisibility(View.VISIBLE);
        }else{
            binding.btnApuntarActividad.setVisibility(View.VISIBLE);
            binding.btnEditarPost.setVisibility(View.GONE);
            binding.btnBorrarPost.setVisibility(View.GONE);
        }
        
        // Set los datos recuperados del Bundle
        binding.detailTitulo.setText(titulo);
        binding.detailAutor.setText(nombreAutor);
        binding.detailDescripcion.setText(descripcion);
        binding.detailFecha.setText(String.format("Fecha y hora: %s", fechaFormateada));
        binding.detailLocalizacion.setText(String.format("Localizacion: %s", localizacion));
        binding.detailNumeroPersonas.setText(String.format("Numero de personas: %s", String.valueOf(numeroPersonas)));
        binding.detailMaterialNecesario.setText(String.format("Material necesario: %s", materialNecesario ? "Si" : "No"));
        
        if(imageUrl != null){
            Glide.with(binding.detailImage.getContext()).load(imageUrl).into(binding.detailImage);    
        }else{
            Glide.with(binding.detailImage.getContext()).load(R.drawable.icon_no_image).into(binding.detailImage);
        }
        
        binding.btnApuntarActividad.setOnClickListener(this);
        
    }
    
    private void comprobarUsuarioRegistrado(String postId, String userId, Button boton){
        DocumentReference postRef = db.collection("posts").document(postId);
        
        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Map<String, Object> usuariosRegistrados = (Map<String, Object>) document.get("usuariosRegistrados");
                        int totalUsuariosRegistrados = usuariosRegistrados != null ? usuariosRegistrados.size() : 0;
                        int maximoUsuariosRegistrados = document.getLong("numeroPersonas").intValue();

                        boolean usuarioEstaRegistrado = usuariosRegistrados != null && usuariosRegistrados.containsKey(userId);

                        if(totalUsuariosRegistrados >= maximoUsuariosRegistrados && !usuarioEstaRegistrado){
                            binding.btnApuntarActividad.setEnabled(false);
                        }else{
                            binding.btnApuntarActividad.setEnabled(true);
                        }
                        
                        if(usuarioEstaRegistrado){
                            estaRegistrado = true;
                            boton.setText("Eliminarse");
                        }else{
                            estaRegistrado = false;
                            boton.setText("Apuntarse");
                        }
                    }
                }
            }
        });
    }
    
    private void operacionRegistro(String postId, String userId){
        RegistroActividad registroActividad = new RegistroActividad();
        EnviarCorreos enviarCorreos = new EnviarCorreos();
        Context context = getContext();
        
        if(estaRegistrado){
            registroActividad.eliminarUsuarioActividad(context, postId, userId);
            binding.btnApuntarActividad.setText("Apuntarse");
            estaRegistrado = false;
        }else{
            registroActividad.registrarUsuarioActividad(context, postId, userId);
            enviarCorreos.enviarEmailApuntarActividad(context, postUserId, userId, titulo);
            binding.btnApuntarActividad.setText("Eliminarse");
            estaRegistrado = true;
        }
        
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.btnApuntarActividad){
            operacionRegistro(postId,userId);
        }
    }
}