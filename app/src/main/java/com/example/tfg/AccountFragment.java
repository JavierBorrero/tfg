package com.example.tfg;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.tfg.databinding.FragmentAccountBinding;
import com.example.tfg.models.Usuario;
import com.example.tfg.utils.EliminarUsuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AccountFragment extends Fragment implements View.OnClickListener {

    /*
        === ACCOUNT FRAGMENT ===
        Esta clase muestra los datos del usuario que ha iniciado sesion en la aplicacion
     */
    
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private MainActivity activity;

    private FragmentAccountBinding binding;

    private TextView email, nombre, apellidos, telefono;
    private ImageView imagen;

    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Asignar los TextView a variables para que el codigo sea mas legible
        email = binding.textEmail;
        nombre = binding.textNombre;
        apellidos = binding.textApellidos;
        telefono = binding.textTelefono;
        imagen = binding.profilePic;

        // Instancias
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // getActivity para viajar entre pantallas
        activity = (MainActivity) getActivity();

        // Guardamos el Id del usuario logeado en una variable
        userId = auth.getCurrentUser().getUid();

        // Llamada para obtener los datos del usuario
        obtenerDatosUsuario();

        // onClicks
        binding.btnLogout.setOnClickListener(this);
        binding.btnMisActividades.setOnClickListener(this);
        binding.btnEditProfile.setOnClickListener(this);
        binding.btnEliminarCuenta.setOnClickListener(this);
    }

    /*
        === FUNCION OBTENER DATOS DEL USUARIO ===
        Esta funcion obtiene los datos del usuario que ha iniciado sesion en la app
     */
    private void obtenerDatosUsuario(){
        // Referencia al documento del usuario por el userId
        DocumentReference docRef = db.collection("usuarios").document(userId);

        // Se obtiene el documento
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Si existe el documento
                if(documentSnapshot.exists()){
                    // Se obtienen los datos del usuario
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    // Se ponen los datos en los TextView
                    email.setText(usuario.getEmail());
                    nombre.setText(usuario.getNombre());
                    apellidos.setText(usuario.getApellido());
                    telefono.setText(String.valueOf(usuario.getTelefono()));
                    // Se pone la foto de perfil del usuario dependiendo de si tiene o no
                    if(usuario.getImagePfpUrl() != null){
                        Glide.with(getContext()).load(usuario.getImagePfpUrl()).into(imagen);
                    }else{
                        Glide.with(getContext()).load(R.drawable.icon_person_profile).into(imagen);
                    }
                }else{
                    Toast.makeText(getContext(), "No existe el documento o error al obtener", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mostrar el primer Popup para eliminar la cuenta del usuario
    private void mostrarPrimerPopupEliminar(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        
        builder.setTitle("Eliminar Usuario");
        builder.setMessage("Va a proceder a eliminar su usuario ¿Desea continuar?");
        
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // En caso de aceptar se pasa al segundo Popup
                mostrarSegundoPopupEliminar();
            }
        });
        
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Mostrar el segundo Popup para eliminar la cuenta del usuario
    private void mostrarSegundoPopupEliminar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        
        builder.setTitle("Eliminar Usuario");
        builder.setMessage("Esta accion es irreversible ¿Esta seguro de que quiere continuar?");
        
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // En caso de aceptar se crea un objeto de la clase EliminarUsuario
                // Y se llama al metodo eliminarUsuario
                EliminarUsuario eliminarUsuario = new EliminarUsuario();
                eliminarUsuario.eliminarUsuario(getContext(), userId);
            }
        });
        
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    
    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        if(i == R.id.btnLogout){
            FirebaseAuth.getInstance().signOut();
            if(activity != null){
                activity.goToFragment(new SignInFragment(), R.id.signinfragment);
            }
        }
        
        if(i == R.id.btnEditProfile){
            if(activity != null){
                activity.goToFragment(new EditProfileFragment(), R.id.editprofilefragment);
            }
        }
        
        if(i == R.id.btnMisActividades){
            if(activity != null){
                activity.goToFragment(new MisActividadesFragment(), R.id.misactividadesfragment);
            }
        }
        
        if(i == R.id.btnEliminarCuenta){
            mostrarPrimerPopupEliminar();
        }
    }
}