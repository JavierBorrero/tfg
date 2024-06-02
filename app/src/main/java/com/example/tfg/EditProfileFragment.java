package com.example.tfg;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.tfg.databinding.FragmentEditProfileBinding;
import com.example.tfg.models.Usuario;
import com.example.tfg.utils.ValidarFormularios;
import com.example.tfg.utils.textwatchers.CustomTextWatcher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment implements View.OnClickListener {

    /*
        === EDIT PROFILE FRAGMENT ===
        Esta clase se encarga de editar los datos del perfil del usuario logeado
     */
    
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference storageReference;
    MainActivity activity;
    
    private FragmentEditProfileBinding binding;
    
    EditText nombre, apellidos, telefono;
    Uri image;
    ImageView imagen;
    String userId, nombreOriginal, apellidosOriginal;
    int telefonoOriginal;
    
    Usuario usuario;
    
    boolean isUploading = false;

    // Esto se encarga junto con el Intent de recoger la foto de la galeria del usuario
    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        if (result.getData() != null) {
                            // Se guarda la foto en el Uri y se pone de imagen previa
                            image = result.getData().getData();
                            imagen.setImageURI(image);
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
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instancias
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = mAuth.getCurrentUser().getUid();
        activity = (MainActivity) getActivity(); 

        // Binding en variables para mejorar la legibilidad del codigo
        nombre = binding.fieldNombre;
        apellidos = binding.fieldApellido;
        telefono = binding.fieldTelefono;
        imagen = binding.profilePic;

        // Llamada al metodo para obtener los datos del usuario
        obtenerDatosUsuario(userId);

        // onClicks
        binding.btnAceptar.setOnClickListener(this);
        binding.btnCancelar.setOnClickListener(this);
        binding.btnEditPfp.setOnClickListener(this);

        // Llamada a textWatchers y checkForChanges
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
        
        binding.fieldNombre.addTextChangedListener(textWatcher);
        binding.fieldApellido.addTextChangedListener(textWatcher);
        binding.fieldTelefono.addTextChangedListener(textWatcher);
    }

    // Metodo que se encarga de comprobar si algun campo ha tenido modificaciones
    private void checkForChanges(){
        // Se guarda el texto actual en variables
        String currentNombre = binding.fieldNombre.getText().toString().trim();
        String currentApellido = binding.fieldApellido.getText().toString().trim();
        
        String numeroTelefono = binding.fieldTelefono.getText().toString().trim();
        int currentNumero = 0;
        
        if(!numeroTelefono.isEmpty()){
            try{
                currentNumero = Integer.parseInt(numeroTelefono);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        // Se compara con el texto original y si cambia se pone en enable el boton del update
        boolean hasChanges = !currentNombre.equals(nombreOriginal) ||
                !currentApellido.equals(apellidosOriginal) ||
                currentNumero != telefonoOriginal ||
                image != null;
        
        if(!hasChanges){
            binding.btnAceptar.setEnabled(false);
            int colorBackground = ContextCompat.getColor(getContext(), R.color.desactivado);
            binding.btnAceptar.setBackgroundColor(colorBackground);
        }else{
            binding.btnAceptar.setEnabled(true);
            int colorBackground = ContextCompat.getColor(getContext(), R.color.verde_boton);
            binding.btnAceptar.setBackgroundColor(colorBackground);
        }
    }
    
    // Metodo para obtener los datos del usuario
    private void obtenerDatosUsuario(String usuarioId){
        // Referencia al documento del usuario
        DocumentReference docRef = db.collection("usuarios").document(usuarioId);

        // Se obtienen los datos
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    usuario = documentSnapshot.toObject(Usuario.class);

                    // Se guardan en variables
                    nombreOriginal = usuario.getNombre();
                    apellidosOriginal = usuario.getApellido();
                    telefonoOriginal = usuario.getTelefono();

                    // Se añaden a los editText
                    nombre.setText(usuario.getNombre());
                    apellidos.setText(usuario.getApellido());
                    telefono.setText(String.valueOf(usuario.getTelefono()));

                    if(usuario.getImagePfpUrl() != null){
                        Glide.with(imagen.getContext()).load(usuario.getImagePfpUrl()).into(imagen);
                    }else{
                        Glide.with(imagen.getContext()).load(R.drawable.icon_person_profile).into(imagen);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // Intent para obtener imagen de la galeria del usuario
    private void lanzarIntentObtenerImagen(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryActivityResultLauncher.launch(intent);
    }
    
    // Metodo para validar los campos
    private boolean validarCampos(){
        // Objeto de la clase ValidarFormularios y se llama al metodo
        ValidarFormularios validarFormularios = new ValidarFormularios();
        
        return validarFormularios.validarEditarPerfil(
                binding.fieldNombre,
                binding.fieldApellido,
                binding.fieldTelefono
        );
    }

    // Metodo para aplicar los cambios
    private void aplicarCambios(){
        // Si la validacion falla no se hace nada
        if(!validarCampos()){
            return;
        }

        // Se deshabilita el boton para evitar doble click
        binding.btnAceptar.setClickable(false);

        // Se guardan los datos en variables
        String cadenaNombre = nombre.getText().toString().trim();
        String cadenaApellido = apellidos.getText().toString().trim();
        int numTelefono = Integer.parseInt(telefono.getText().toString().trim());

        /*
            Si se ha seleccionado una imagen se sube y luego se actualiza el perfil
            Y si no se ha seleccionado se actualiza directamente el perfil
         */
        if(image != null){
            editarConFotoPerfil(userId, cadenaNombre, cadenaApellido, numTelefono, image);
        }else{
            editarPerfil(userId, cadenaNombre, cadenaApellido, numTelefono, null);
        }
    }

    // Metodo para actualizar el perfil
    private void editarPerfil(String usuarioId, String nombre, String apellido, int telefono, @Nullable String imageUrl){
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", nombre);
        user.put("apellido", apellido);
        user.put("telefono", telefono);

        // Si se ha seleccionado una foto se añade la url
        if(imageUrl != null){
            user.put("imagePfpUrl", imageUrl);
        }

        // Se hace el update
        db.collection("usuarios").document(usuarioId)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Perfil editado con exito", Toast.LENGTH_SHORT).show();
                        activity.goToFragment(new AccountFragment(), R.id.accountfragment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        
    }

    // Metodo para subir la foto y luego se actualizan los datos
    private void editarConFotoPerfil(String usuarioId, String nombre, String apellido, int telefono, @Nullable Uri file){
        if(file != null){
            // Referencia a la ubicacion de la foto de perfil
            StorageReference reference = storageReference.child("images/"+userId+"/pfp/");
            reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Se coge la url de la imagen
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Se guarda en una variable
                            String imageUrl = uri.toString();
                            // Se pasa al metodo para actualizar el perfil
                            editarPerfil(usuarioId, nombre, apellido, telefono, imageUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            // Editar el perfil sin foto
                            editarPerfil(usuarioId, nombre, apellido, telefono, null);
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

    // Metodo para desactivar el boton y evitar doble click
    private void disableButtonForDelay(long delayMillis){
        binding.btnAceptar.setEnabled(false);
        binding.btnAceptar.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.btnAceptar.setEnabled(true);
                isUploading = false;
            }
        }, delayMillis);
    }
    
    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        if(i == R.id.btnEditPfp){
          lanzarIntentObtenerImagen();
        }
        
        if(i == R.id.btnCancelar){
            if(activity != null){
                activity.goToFragment(new AccountFragment(), R.id.accountfragment);
            }
        }
        
        if(i == R.id.btnAceptar){
            if(!isUploading){
                isUploading = true;
                disableButtonForDelay(1000);
                aplicarCambios();
            }
        }
    }
}