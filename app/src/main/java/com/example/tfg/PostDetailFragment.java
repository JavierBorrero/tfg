package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.tfg.databinding.FragmentPostDetailBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostDetailFragment extends Fragment {
    
    FragmentPostDetailBinding binding;
    
    String titulo, descripcion, localizacion, nombreAutor, fechaFormateada, imageUrl;
    Date fecha;
    long fechaLong;
    int numeroPersonas;
    boolean materialNecesario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Recuperar datos del Bundle
        if(getArguments() != null){
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
        
        // Set los datos recuperados del Bundle
        binding.detailTitulo.setText(titulo);
        binding.detailAutor.setText(nombreAutor);
        binding.detailDescripcion.setText(descripcion);
        binding.detailFecha.setText(String.format("Fecha y hora: %s", fechaFormateada));
        binding.detailLocalizacion.setText(String.format("Localizacion: %s", localizacion));
        binding.detailNumeroPersonas.setText(String.format("Numero de personas: %s", String.valueOf(numeroPersonas)));
        binding.detailMaterialNecesario.setText(String.format("Material necesario: %s", materialNecesario ? "Si" : "No"));
        Glide.with(binding.detailImage.getContext()).load(imageUrl).into(binding.detailImage);
        
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}