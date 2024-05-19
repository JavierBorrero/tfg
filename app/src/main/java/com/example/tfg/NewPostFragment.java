package com.example.tfg;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tfg.databinding.FragmentNewPostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewPostFragment extends Fragment implements View.OnClickListener {
    
    FirebaseAuth auth;
    FirebaseFirestore db;
    StorageReference storageReference;
    MainActivity activity;
    
    private FragmentNewPostBinding binding;

    EditText titulo, descripcion, localizacion, fechaHora, imagen, personas;
    
    CheckBox material;
    
    ImageView fotoPrevia;
    
    Timestamp firebaseTimeStamp;
    Uri image;
    
    String userId;

    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        if(result.getData() != null){
                            image = result.getData().getData();
                            fotoPrevia.setImageURI(image);
                        }
                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        binding = FragmentNewPostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = auth.getCurrentUser().getUid();
        activity = (MainActivity) getActivity(); 
        

        binding.btnSubmitPost.setOnClickListener(this);
        binding.fieldFechaHora.setOnClickListener(this);
        binding.fieldImagen.setOnClickListener(this);
        

        titulo = binding.fieldTitulo;
        descripcion = binding.fieldDescripcion;
        localizacion = binding.fieldLocalizacion;
        fechaHora = binding.fieldFechaHora;
        imagen = binding.fieldImagen;
        fotoPrevia = binding.fotoPrevia;
        personas = binding.fieldNumeroPersonas;
        material = binding.fieldMaterial;

    }

    // === INICIO VALIDACION ===
    private boolean validarCampos(){
        boolean validar = true;
        
        String tituloTrim = titulo.getText().toString().trim();
        String descripcionTrim = descripcion.getText().toString().trim();
        String localizacionTrim = localizacion.getText().toString().trim();
        String personasTrim = personas.getText().toString().trim();
        String cadenaFechaHora = fechaHora.getText().toString();
        

        // Comprobar que el campo titulo no esta vacio
        if(tituloTrim.isEmpty()){
            titulo.setError("Titulo vacio");
            validar = false;
        }else{
            titulo.setError(null);
        }

        // Comprobar que el campo descripcion no esta vacio
        if(descripcionTrim.isEmpty()){
            descripcion.setError("Descripcion vacia");
            validar = false;
        }else{
            descripcion.setError(null);
        }

        // Comprobar que el campo localizacion no esta vacio
        if(localizacionTrim.isEmpty()){
            localizacion.setError("Localizacion vacia");
            validar = false;
        }else{
            localizacion.setError(null);
        }
        
        // Comprobar que se selecciona una fecha y hora
        if(cadenaFechaHora.isEmpty()){
            fechaHora.setError("Fecha y hora vacias");
            validar = false;
        }else{
            fechaHora.setError(null);
        }

        /*
            Comprobar que el campo personas no esta vacio
            Si no esta vacio comprobar que el numero es valido
         */
        if(personasTrim.isEmpty()){
            personas.setError("Debe indicar un número de personas");
            validar = false;
        } else if (!validarNumeroPersonas()) {
            validar = false;
        }else{
            personas.setError(null);
        }

        return validar;
    }
    
    // Funcion para validar el numero de personas que se pueden apuntar a la actividad
    private boolean validarNumeroPersonas(){
        boolean validar = true;

        int numeroPersonas = Integer.parseInt(personas.getText().toString());

        if (numeroPersonas <= 0 || numeroPersonas > 10){
            personas.setError("Número entre 1 y 10");
            validar = false;
        }else{
            personas.setError(null);
        }

        return validar;
    }
    // === FIN VALIDACION ===
    
    
    /*
        Funcion para obtener la fecha y la hora
        Se guarda en la variable Timestamp firebaseTimeStamp
     */
    private void obtenerFechaYHora(){
        
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int  minute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth, hourOfDay, minute, 0);
                        
                        Date date = calendar.getTime();
                        
                        firebaseTimeStamp = new Timestamp(date);
                        
                        fechaHora.setText(dayOfMonth+"/"+(month+1)+"/"+year+" - "+hourOfDay+":"+minute);
                    }
                }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        }, mYear, mMonth, mDay);
        
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
        
    }

    private void lanzarIntentObtenerImagen(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryActivityResultLauncher.launch(intent);
    }
    
    private void publicarPost(){
        if(!validarCampos()){
            return;
        }

        String cadenaTitulo = titulo.getText().toString().trim();
        String cadenaDescripcion = descripcion.getText().toString().trim();
        String cadenaLocalizacion = localizacion.getText().toString().trim();
        int numPersonas = Integer.parseInt(personas.getText().toString());
        boolean materialNecesario = material.isChecked();
        
        if(image != null){
            datosPostImagen(userId, cadenaTitulo, cadenaDescripcion, cadenaLocalizacion, numPersonas, materialNecesario, image);
        }else{
            datosPost(userId, cadenaTitulo, cadenaDescripcion, cadenaLocalizacion, numPersonas, materialNecesario, null);
        }
        
    }
    
    private void datosPost(String usuarioId, String titulo, String descripcion, String localizacion, int personas, boolean material, @Nullable String imageUrl){
        Map<String, Object> post = new HashMap<>();
        post.put("userId", usuarioId);
        post.put("titulo", titulo);
        post.put("descripcion", descripcion);
        post.put("localizacion", localizacion);
        post.put("numeroPersonas", personas);
        post.put("materialNecesario", material);
        post.put("fecha", firebaseTimeStamp);
        
        if(imageUrl != null){
            post.put("imageUrl", imageUrl);
        }

        db.collection("posts").document()
                .set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Post publicado", Toast.LENGTH_SHORT).show();
                        activity.goToFragment(new PostsFragment(), R.id.postsfragment);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void datosPostImagen(String usuarioId, String titulo, String descripcion, String localizacion, int personas, boolean material, @Nullable Uri file){
        if(file != null){
            StorageReference reference = storageReference.child("images/"+userId+"/user_posts/"+file.getLastPathSegment());
            reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            datosPost(usuarioId, titulo, descripcion, localizacion, personas, material, imageUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            // Guardar el post sin imagen
                            datosPost(usuarioId, titulo, descripcion, localizacion, personas, material, null);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    @Override
    public void onClick(View view) {
        int i = view.getId();

        if(i == R.id.btnSubmitPost){
            publicarPost();
        }

        if(i == R.id.fieldFechaHora){
            obtenerFechaYHora();
        }

        if(i == R.id.fieldImagen){
            lanzarIntentObtenerImagen();
        }
    }
}