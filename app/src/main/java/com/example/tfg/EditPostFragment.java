package com.example.tfg;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tfg.databinding.FragmentEditPostBinding;
import com.example.tfg.utils.textwatchers.CustomOnCheckedChangeListener;
import com.example.tfg.utils.textwatchers.CustomTextWatcher;
import com.example.tfg.utils.EnviarCorreos;
import com.example.tfg.utils.ValidarFormularios;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditPostFragment extends Fragment implements View.OnClickListener {

    /*
        === EDIT POST FRAGMENT ===
        Esta clase se encarga de editar los datos del  post
     */
    
    FragmentEditPostBinding binding;
    MainActivity activity;
    FirebaseAuth auth;
    FirebaseFirestore db;
    StorageReference storageReference;
    
    String postId, postUserId, titulo, descripcion, localizacion, nombreAutor, imageUrl, fechaString;
    int numeroPersonas, usuariosRegistrados;
    boolean materialNecesario;
    Date fecha;
    long fechaLong;
    Uri uri;
    Timestamp firebaseTimeStamp, oldFirebaseTimeStamp;
    ArrayList<String> emailList;
    
    boolean isUploading = false;

    // Esto se encarga junto con el Intent de recoger la foto de la galeria del usuario
    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        if (result.getData() != null) {
                            // Se guarda la foto en el Uri y se pone en la foto previa
                            uri = result.getData().getData();
                            binding.fotoPrevia.setImageURI(uri);
                            checkForChanges();
                        }
                    }
                }
            });
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditPostBinding.inflate(inflater, container, false);

        // Instancias
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        activity = (MainActivity) getActivity();

        // Se recogen los datos del Bundle
        if(getArguments() != null){
            postId = getArguments().getString("id");
            postUserId = getArguments().getString("userId");
            titulo = getArguments().getString("titulo");
            descripcion = getArguments().getString("descripcion");
            localizacion = getArguments().getString("localizacion");
            fechaLong = getArguments().getLong("fecha");
            fecha = new Date(fechaLong);
            oldFirebaseTimeStamp = new Timestamp(fecha);
            fechaString = getArguments().getString("fechaString");
            numeroPersonas = getArguments().getInt("numeroPersonas");
            materialNecesario = getArguments().getBoolean("materialNecesario");
            nombreAutor = getArguments().getString("nombreAutor");
            imageUrl = getArguments().getString("imageUrl");
            usuariosRegistrados = getArguments().getInt("usuariosRegistrados");
            emailList = getArguments().getStringArrayList("emailList");
        }
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Se ponen los textos del Bundle en los EditText
        binding.editTitulo.setText(titulo);
        binding.editDescripcion.setText(descripcion);
        binding.editLocalizacion.setText(localizacion);
        binding.editFechaHora.setText(fechaString);
        binding.editNumeroPersonas.setText(String.valueOf(numeroPersonas));
        binding.editMaterial.setChecked(materialNecesario);

        // Si tiene imagen o no
        if(imageUrl != null){
            Glide.with(binding.fotoPrevia.getContext()).load(imageUrl).into(binding.fotoPrevia);
        }else{
            Glide.with(binding.fotoPrevia.getContext()).load(R.drawable.icon_no_image).into(binding.fotoPrevia);
        }

        // onClicks
        binding.btnConfirmEdit.setOnClickListener(this);
        binding.editFechaHora.setOnClickListener(this);
        binding.editImagen.setOnClickListener(this);

        // Llamada a los metodos
        addTextWatchers();
        checkForChanges();
    }

    /*
        Metodo que se encarga de añadir los TextWatcher
        Esto sirve para evitar que sin modificar ningun campo los usuarios puedan hacer un update
        Y evitar cargas en la bd
     */
    private void addTextWatchers(){
        CustomTextWatcher textWatcher = new CustomTextWatcher(this::checkForChanges);
        
        binding.editTitulo.addTextChangedListener(textWatcher);
        binding.editDescripcion.addTextChangedListener(textWatcher);
        binding.editLocalizacion.addTextChangedListener(textWatcher);
        binding.editNumeroPersonas.addTextChangedListener(textWatcher);
        binding.editMaterial.setOnCheckedChangeListener(new CustomOnCheckedChangeListener(this::checkForChanges));
    }

    // Metodo que se encarga de comprobar si algun campo ha tenido modificaciones
    private void checkForChanges(){

        // Se guarda el texto actual en variables
        String currentTitulo = binding.editTitulo.getText().toString().trim();
        String currentDescripcion = binding.editDescripcion.getText().toString().trim();
        String currentLocalizacion = binding.editLocalizacion.getText().toString().trim();
        String currentFecha = binding.editFechaHora.getText().toString().trim();
        
        String numeroPersonasText = binding.editNumeroPersonas.getText().toString().trim();
        int currentNumeroPersonas = 0;
        
        if(!numeroPersonasText.isEmpty()){
            try{
                currentNumeroPersonas = Integer.parseInt(numeroPersonasText);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        boolean currentMaterial = binding.editMaterial.isChecked();

        // Se compara con el texto original y si cambia se pone en enable el boton del update
        boolean hasChanges = !currentTitulo.equals(titulo) ||
                !currentDescripcion.equals(descripcion) ||
                !currentLocalizacion.equals(localizacion) ||
                !currentFecha.equals(fechaString) ||
                currentNumeroPersonas != numeroPersonas ||
                currentMaterial != materialNecesario ||
                uri != null;
        
        binding.btnConfirmEdit.setEnabled(hasChanges);
    }

    // Metodo para obtener la fecha y hora
    private void obtenerFechaYHora(){

        /*
            Despliega un DatePickerDialog y luego un TimePickerDialog
            para poder seleccionar fecha y hora
         */
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

                        // Se guarda le fecha seleccionada
                        firebaseTimeStamp = new Timestamp(date);

                        binding.editFechaHora.setText(dayOfMonth+"/"+(month+1)+"/"+year+" - "+hourOfDay+":"+minute);
                        checkForChanges();
                    }
                }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        }, mYear, mMonth, mDay);

        // Fecha minima para evitar que no se pueda escoger una fecha menor al dia de hoy
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();

    }

    // Intent para obtener la imagen de la galeria de un usuario
    private void lanzarIntentObtenerImagen(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryActivityResultLauncher.launch(intent);
    }

    // Validacion de campos del EditPost
    private boolean validarCampos(){
        // Objeto de la clase ValidarFormularios y se llama al metodo para validar
        ValidarFormularios validarFormularios = new ValidarFormularios();
        
        return validarFormularios.validarEditarPost(
                binding.editTitulo,
                binding.editDescripcion,
                binding.editLocalizacion,
                binding.editFechaHora,
                binding.editNumeroPersonas,
                usuariosRegistrados
        );
    }

    // Metodo para aplicar la Edicion del Post
    private void aplicarEdicion(){
        // Si la validacion falla no se hace nada
        if(!validarCampos()){
            return;
        }

        // Se quita el clickable para evitar dobleClick y doble subida
        binding.btnConfirmEdit.setClickable(false);

        // Se guardan los datos en variables
        String cadenaTitulo = binding.editTitulo.getText().toString().trim();
        String cadenaDescripcion = binding.editDescripcion.getText().toString().trim();
        String cadenaLocalizacion = binding.editLocalizacion.getText().toString().trim();
        int numPersonas = Integer.parseInt(binding.editNumeroPersonas.getText().toString());
        boolean materialNecesario = binding.editMaterial.isChecked();

        /*
            Si se ha puesto una imagen se sube la imagen y luego se publica la edicion
            Si no se publica directamente la edicion
         */
        if(uri != null){
            subirImagenyPublicarEdicion(postId, postUserId, cadenaTitulo, cadenaDescripcion, cadenaLocalizacion, numPersonas, materialNecesario, uri);
        }else{
            publicarEdicion(postId, postUserId, cadenaTitulo, cadenaDescripcion, cadenaLocalizacion, numPersonas, materialNecesario, null);
        }
    }

    // Metodo que se encarga de subir la edicion
    private void publicarEdicion(String documentId, String postUserId, String titulo, String descripcion, String localizacion, int personas, boolean material, @Nullable String url){
        // Se crea el mapa con los datos
        Map<String, Object> post = new HashMap<>();
        post.put("userId", postUserId);
        post.put("titulo", titulo);
        post.put("descripcion", descripcion);
        post.put("localizacion", localizacion);
        post.put("numeroPersonas", personas);
        post.put("materialNecesario", material);

        // Si hay fecha nueva se pone la nueva y si no se pone la antigua
        if(firebaseTimeStamp != null){
            post.put("fecha", firebaseTimeStamp);
        }else{
            post.put("fecha", oldFirebaseTimeStamp);
        }

        // Si hay imagen se guarda
        if(url != null){
            post.put("imageUrl", url);
        }

        // Se hace el update
        db.collection("posts").document(documentId)
                .update(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        correoActualizar(emailList);
                        Toast.makeText(getContext(), "Post editado con exito", Toast.LENGTH_SHORT).show();
                        activity.goToFragment(new PostsFragment(), R.id.postsfragment);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // En caso de que el post tenga imagen se sube
    private void subirImagenyPublicarEdicion(String documentId, String postUserId, String titulo, String descripcion, String localizacion, int personas, boolean material, @Nullable Uri file){
        if(file != null){
            // Referencia a la ubicacion de la foto
            StorageReference reference = storageReference.child("images/"+postUserId+"/user_posts/"+file.getLastPathSegment());
            reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Se coge la url de la referencia
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Se guarda en una variable y se pasa al metodo publicarEdicion
                            String imageUrl = uri.toString();
                            publicarEdicion(documentId, postUserId, titulo, descripcion, localizacion, personas, material, imageUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            publicarEdicion(documentId, postUserId, titulo, descripcion, localizacion, personas, material, null);
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

    // Metodo para mandar correos al actualizar el post
    private void correoActualizar(ArrayList<String> emailList){
        // Se envia un correo por cada email en el emailList
        EnviarCorreos enviarCorreos = new EnviarCorreos();
        for(String email : emailList){
            enviarCorreos.enviarCorreoEditarPost(getContext(), postUserId, email, titulo);
        }
        enviarCorreos.mostrarPopupMensaje(getContext(), "Se ha enviado un mensaje a los usuarios sobre la actualizacion de la actividad");
    }

    // Metodo para desactivar el boton despues de confirmar el update
    private void disableButtonForDelay(long delayMillis){
        binding.btnConfirmEdit.setEnabled(false);
        binding.btnConfirmEdit.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.btnConfirmEdit.setEnabled(true);
                isUploading = false;
            }
        }, delayMillis);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        if(i == R.id.btnConfirmEdit){
            if(!isUploading){
                isUploading = true;
                disableButtonForDelay(1000);
                aplicarEdicion();
            }
        }
        
        if(i == R.id.editFechaHora){
            obtenerFechaYHora();
        }
        
        if(i == R.id.editImagen){
            lanzarIntentObtenerImagen();
        }
    }
}