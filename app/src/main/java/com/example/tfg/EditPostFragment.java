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
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tfg.databinding.FragmentEditPostBinding;
import com.example.tfg.utils.ValidarFormularios;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditPostFragment extends Fragment implements View.OnClickListener {
    
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
    
    boolean isUploading = false;

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        if (result.getData() != null) {
                            uri = result.getData().getData();
                            binding.fotoPrevia.setImageURI(uri);
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
        
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        activity = (MainActivity) getActivity();

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
            
        }
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.editTitulo.setText(titulo);
        binding.editDescripcion.setText(descripcion);
        binding.editLocalizacion.setText(localizacion);
        binding.editFechaHora.setText(fechaString);
        binding.editNumeroPersonas.setText(String.valueOf(numeroPersonas));
        binding.editMaterial.setChecked(materialNecesario);
        
        if(imageUrl != null){
            Glide.with(binding.fotoPrevia.getContext()).load(imageUrl).into(binding.fotoPrevia);
        }else{
            Glide.with(binding.fotoPrevia.getContext()).load(R.drawable.icon_no_image).into(binding.fotoPrevia);
        }

        binding.btnConfirmEdit.setOnClickListener(this);
        binding.editFechaHora.setOnClickListener(this);
        binding.editImagen.setOnClickListener(this);
        
    }

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

                        binding.editFechaHora.setText(dayOfMonth+"/"+(month+1)+"/"+year+" - "+hourOfDay+":"+minute);
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
    
    private boolean validarCampos(){
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
    
    private void aplicarEdicion(){
        if(!validarCampos()){
            return;
        }

        binding.btnConfirmEdit.setClickable(false);
        
        String cadenaTitulo = binding.editTitulo.getText().toString().trim();
        String cadenaDescripcion = binding.editDescripcion.getText().toString().trim();
        String cadenaLocalizacion = binding.editLocalizacion.getText().toString().trim();
        int numPersonas = Integer.parseInt(binding.editNumeroPersonas.getText().toString());
        boolean materialNecesario = binding.editMaterial.isChecked();
        
        if(uri != null){
            subirImagenyPublicarEdicion(postId, postUserId, cadenaTitulo, cadenaDescripcion, cadenaLocalizacion, numPersonas, materialNecesario, uri);
        }else{
            publicarEdicion(postId, postUserId, cadenaTitulo, cadenaDescripcion, cadenaLocalizacion, numPersonas, materialNecesario, null);
        }
    }
    
    private void publicarEdicion(String documentId, String postUserId, String titulo, String descripcion, String localizacion, int personas, boolean material, @Nullable String url){
        Map<String, Object> post = new HashMap<>();
        post.put("userId", postUserId);
        post.put("titulo", titulo);
        post.put("descripcion", descripcion);
        post.put("localizacion", localizacion);
        post.put("numeroPersonas", personas);
        post.put("materialNecesario", material);
        
        if(firebaseTimeStamp != null){
            post.put("fecha", firebaseTimeStamp);
        }else{
            post.put("fecha", oldFirebaseTimeStamp);
        }
        
        if(url != null){
            post.put("imageUrl", url);
        }
        
        db.collection("posts").document(documentId)
                .update(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
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
    
    private void subirImagenyPublicarEdicion(String documentId, String postUserId, String titulo, String descripcion, String localizacion, int personas, boolean material, @Nullable Uri file){
        if(file != null){
            StorageReference reference = storageReference.child("images/"+postUserId+"/user_posts/"+file.getLastPathSegment());
            reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
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