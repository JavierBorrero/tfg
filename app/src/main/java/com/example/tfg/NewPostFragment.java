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
import com.example.tfg.utils.ValidarFormularios;
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

    /*
        === NEW POST FRAGMENT ===
        Esta clase se encarga de crear nuevos posts
     */
    
    FirebaseAuth auth;
    FirebaseFirestore db;
    StorageReference storageReference;
    MainActivity activity;
    FragmentNewPostBinding binding;

    EditText titulo, descripcion, localizacion, fechaHora, imagen, personas;
    
    CheckBox material;
    
    ImageView fotoPrevia;
    
    Timestamp firebaseTimeStamp;
    Uri image;
    
    String userId;
    
    private boolean isUploading = false;

    // Esto se encarga de recoger la foto junto con el Intent
    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        if(result.getData() != null){
                            // Se guardan los datos y se muestra la foto previa
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

        // Instancias
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        activity = (MainActivity) getActivity();

        // Se guarda el userId en una variable
        userId = auth.getCurrentUser().getUid();

        // onClicks
        binding.btnSubmitPost.setOnClickListener(this);
        binding.fieldFechaHora.setOnClickListener(this);
        binding.fieldImagen.setOnClickListener(this);
        
        // Binding en variables para falicitar la legibilidad
        titulo = binding.fieldTitulo;
        descripcion = binding.fieldDescripcion;
        localizacion = binding.fieldLocalizacion;
        fechaHora = binding.fieldFechaHora;
        imagen = binding.fieldImagen;
        fotoPrevia = binding.fotoPrevia;
        personas = binding.fieldNumeroPersonas;
        material = binding.fieldMaterial;

    }

    // Metodo para validar los campos del nuevo post
    private boolean validarCampos(){
        // Objeto de la clase ValidarFormularios y llamada al metodo
        ValidarFormularios validarFormularios = new ValidarFormularios();
        
        return validarFormularios.validarNuevoPost(
                titulo,
                descripcion,
                localizacion,
                personas,
                fechaHora
        );
    }
    
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

        /*
            Se muestra un DatePickerDialog y luego un TimePicker dialog
            Para guardar la fecha y la hora
         */
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
        // Se pone una fecha minima para evitar que se suban anuncios con fecha anterior a la de hoy
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
        
    }

    // Intent para obtener la imagen
    private void lanzarIntentObtenerImagen(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryActivityResultLauncher.launch(intent);
    }

    // Metodo para publicar el post
    private void publicarPost(){
        // Si la validacion falla no se hace nada
        if(!validarCampos()){
            return;
        }

        // Se desactiva el boton para evitar el doble click
        binding.btnSubmitPost.setClickable(false);

        // Se guardan los datos en variables
        String cadenaTitulo = titulo.getText().toString().trim();
        String cadenaDescripcion = descripcion.getText().toString().trim();
        String cadenaLocalizacion = localizacion.getText().toString().trim();
        int numPersonas = Integer.parseInt(personas.getText().toString());
        boolean materialNecesario = material.isChecked();

        /*
            Si viene imagen se guarda la imagen y luego se sube el post
            Si no se sube directamente el post
         */
        if(image != null){
            datosPostImagen(userId, cadenaTitulo, cadenaDescripcion, cadenaLocalizacion, numPersonas, materialNecesario, image);
        }else{
            datosPost(userId, cadenaTitulo, cadenaDescripcion, cadenaLocalizacion, numPersonas, materialNecesario, null);
        }
        
    }

    // Metodo para subir el post
    private void datosPost(String usuarioId, String titulo, String descripcion, String localizacion, int personas, boolean material, @Nullable String imageUrl){
        Map<String, Object> post = new HashMap<>();
        Map<String, Object> usuariosRegistrados = new HashMap<>();
        post.put("userId", usuarioId);
        post.put("titulo", titulo);
        post.put("descripcion", descripcion);
        post.put("localizacion", localizacion);
        post.put("numeroPersonas", personas);
        post.put("materialNecesario", material);
        post.put("fecha", firebaseTimeStamp);
        post.put("usuariosRegistrados", usuariosRegistrados);

        // Si viene imagen se añade
        if(imageUrl != null){
            post.put("imageUrl", imageUrl);
        }

        // Se sube el post
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

    // Se sube la imagen
    private void datosPostImagen(String usuarioId, String titulo, String descripcion, String localizacion, int personas, boolean material, @Nullable Uri file){
        if(file != null){
            StorageReference reference = storageReference.child("images/"+userId+"/user_posts/"+file.getLastPathSegment());
            reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Se coge la url de la imagen
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Se guarda la url en una variable
                            String imageUrl = uri.toString();
                            // Se llama al metodo para subir el post
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

    // Metodo para desactivar el boton para evitar el doble click
    private void disableButtonForDelay(long delayMillis){
        binding.btnSubmitPost.setEnabled(false);
        binding.btnSubmitPost.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.btnSubmitPost.setEnabled(true);
                isUploading = false;
            }
        }, delayMillis);
    }
    
    @Override
    public void onClick(View view) {
        int i = view.getId();

        if(i == R.id.btnSubmitPost){
            if(!isUploading){
                isUploading = true;
                disableButtonForDelay(1000);
                publicarPost();   
            }
        }

        if(i == R.id.fieldFechaHora){
            obtenerFechaYHora();
        }

        if(i == R.id.fieldImagen){
            lanzarIntentObtenerImagen();
        }
    }
}