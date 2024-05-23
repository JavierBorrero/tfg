package com.example.tfg;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AccountFragment extends Fragment implements View.OnClickListener {
    
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference storageReference;
    MainActivity activity;
    
    private FragmentAccountBinding binding;
    
    TextView email, nombre, apellidos;
    
    ImageView imagen;
    
    String userId;

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
        
        email = binding.textEmail;
        nombre = binding.textNombre;
        apellidos = binding.textApellidos;
        imagen = binding.profilePic;

        // Instancias
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        activity = (MainActivity) getActivity();
        
        storageReference = FirebaseStorage.getInstance().getReference();
        
        obtenerDatosUsuario();
        
        binding.btnLogout.setOnClickListener(this);
        binding.btnEditProfile.setOnClickListener(this);
    }
    
    private void obtenerDatosUsuario(){
        
        userId = mAuth.getCurrentUser().getUid();

        DocumentReference docRef = db.collection("usuarios").document(userId);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    email.setText(usuario.getEmail());
                    nombre.setText(usuario.getNombre());
                    apellidos.setText(usuario.getApellido());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        
        StorageReference reference = storageReference.child("images/"+userId+"/pfp/");
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(imagen.getContext()).load(uri).into(imagen);
            }
        });
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
    }
}