package com.example.tfg;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tfg.databinding.FragmentPostDetailBinding;
import com.example.tfg.models.Usuario;
import com.example.tfg.utils.DescargarPdf;
import com.example.tfg.utils.EnviarCorreos;
import com.example.tfg.utils.RegistroActividad;
import com.example.tfg.utils.adapters.UserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostDetailFragment extends Fragment implements View.OnClickListener, UserAdapter.OnUserLongClickListener, UserAdapter.OnItemClickListener {

    /*
        === POST DETAIL FRAGMENT ===
        En esta clase se ven los detalles del post
     */

    FragmentPostDetailBinding binding;
    UserAdapter userAdapter;
    List<Usuario> userList;

    String postId, userId, postUserId, titulo, descripcion, localizacion, nombreAutor, apellidoAutor, fechaFormateada, imageUrl, userIdAuth;
    Date fecha;
    long fechaLong;
    int numeroPersonas;
    boolean materialNecesario, estaRegistrado;
    Map<String, Object> usuariosRegistrados;

    FirebaseAuth auth;
    FirebaseFirestore db;
    MainActivity activity;

    private EnviarCorreos enviarCorreos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostDetailBinding.inflate(inflater, container, false);

        // Instancias
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        activity = (MainActivity) getActivity();

        // Se obtienen los datos del Bundle
        if(getArguments() != null){
            postId = getArguments().getString("id");
            postUserId = getArguments().getString("userId");
            userId = auth.getCurrentUser().getUid();

            // Llamada al metodo para comprobar si el usuario logeado esta registrado en el post
            comprobarUsuarioRegistrado(postId, userId, binding.btnActividad);
            
            binding.btnActividad.setOnClickListener(this);
        }
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Se guarda el id del usuario registrado en una variable
        String currentUser = auth.getCurrentUser().getUid();

        /*
            - Nueva lista de usuarios
            -  Se crea el adapter con el onClick
            - Se añade el adapter al recycler
         */
        userList = new ArrayList<>();
        binding.recyclerPersonas.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(userList, postUserId, this, this);
        binding.recyclerPersonas.setAdapter(userAdapter);
        
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
            apellidoAutor = getArguments().getString("apellidoAutor");
            imageUrl = getArguments().getString("imageUrl");
            
            // Formatear la fecha
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            fechaFormateada = dateFormat.format(fecha);
        }

        /*
            Si el usuario registrado es el dueño del post:
            - Se muestran los botones de editar y borrar

            Si el usuario registrado NO es el dueño del post:
            - Se muestran los botones de apuntarse y descargar pdf
         */
        if(currentUser.equals(postUserId)){
            binding.btnActividad.setVisibility(View.GONE);
            binding.btnDescargarPdf.setVisibility(View.GONE);
            binding.btnEditarPost.setVisibility(View.VISIBLE);
            binding.btnBorrarPost.setVisibility(View.VISIBLE);
        }else{
            binding.btnActividad.setVisibility(View.VISIBLE);
            binding.btnDescargarPdf.setVisibility(View.VISIBLE);
            binding.btnEditarPost.setVisibility(View.GONE);
            binding.btnBorrarPost.setVisibility(View.GONE);
        }
        
        // Set los datos recuperados del Bundle
        binding.detailTitulo.setText(titulo);
        binding.detailAutor.setText(String.format("%s %s", nombreAutor, apellidoAutor));
        binding.detailDescripcion.setText(String.format("Descripcion: %s", descripcion));
        binding.detailFecha.setText(String.format("Fecha y hora: %s", fechaFormateada));
        binding.detailLocalizacion.setText(String.format("Localizacion: %s", localizacion));
        binding.detailNumeroPersonas.setText(String.format("Numero de personas: %s", String.valueOf(numeroPersonas)));
        binding.detailMaterialNecesario.setText(String.format("Material necesario: %s", materialNecesario ? "Si" : "No"));

        // Si tiene imagen se carga
        if(imageUrl != null){
            Glide.with(binding.detailImage.getContext()).load(imageUrl).into(binding.detailImage);    
        }else{
            Glide.with(binding.detailImage.getContext()).load(R.drawable.icon_no_image).into(binding.detailImage);
        }

        // onClicks
        binding.btnActividad.setOnClickListener(this);
        binding.btnDescargarPdf.setOnClickListener(this);
        binding.btnEditarPost.setOnClickListener(this);
        binding.btnBorrarPost.setOnClickListener(this);
    }

    /*
        El metodo comprueba si el usuario esta apuntado en la
        Actividad y se actualiza la interfaz dependiendo de si esta apuntado o no
     */
    private void comprobarUsuarioRegistrado(String postId, String userId, Button boton){
        // Referencia del documento
        DocumentReference postRef = db.collection("posts").document(postId);

        //Se obtiene el documento
        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    // Si existe el documento
                    if(document.exists()){
                        /*
                            Se obtienen los usuarios registrados y se comparan con los usuarios maximos
                            Si el usuario aun tiene espacio para apuntarse, se podra apuntar.
                            En caso de que no tenga espacio, el boton se queda en disable
                         */
                        usuariosRegistrados = (Map<String, Object>) document.get("usuariosRegistrados");
                        int totalUsuariosRegistrados = usuariosRegistrados != null ? usuariosRegistrados.size() : 0;
                        int maximoUsuariosRegistrados = document.getLong("numeroPersonas").intValue();

                        boolean usuarioEstaRegistrado = usuariosRegistrados != null && usuariosRegistrados.containsKey(userId);

                        if(totalUsuariosRegistrados >= maximoUsuariosRegistrados && !usuarioEstaRegistrado){
                            binding.btnActividad.setEnabled(false);
                        }else{
                            binding.btnActividad.setEnabled(true);
                        }

                        // Cambiar el texto segun si esta apuntado o no
                        if(usuarioEstaRegistrado){
                            estaRegistrado = true;
                            boton.setText("Eliminarse");
                        }else{
                            estaRegistrado = false;
                            boton.setText("Apuntarse");
                        }
                        
                        if(usuariosRegistrados != null){
                            for(String userId : usuariosRegistrados.keySet()){
                                infoUsuarioRecycler(userId);
                            }
                        }
                    }
                }
            }
        });
    }

    // Metodo para registrar a los usuarios
    private void operacionRegistro(String postId, String userId){
        RegistroActividad registroActividad = new RegistroActividad();
        enviarCorreos = new EnviarCorreos();
        Context context = getContext();

        // Si esta registrado se elimina
        if(estaRegistrado){
            registroActividad.eliminarUsuarioActividad(context, postId, userId);
            enviarCorreos.enviarEmailUsuarioActividad(context, postUserId, userId, titulo, false);
            binding.btnActividad.setText("Apuntarse");
            estaRegistrado = false;
        }else{
            // Si no esta registrado se añade
            registroActividad.registrarUsuarioActividad(context, postId, userId);
            enviarCorreos.enviarEmailUsuarioActividad(context, postUserId, userId, titulo, true);
            binding.btnActividad.setText("Eliminarse");
            estaRegistrado = true;
        }

        if(activity != null){
            activity.goToFragment(new PostsFragment(), R.id.postsfragment);
        }
    }

    // Metodo para obtener los usuarios apuntados
    private void infoUsuarioRecycler(String userId){
        // Referencia al documento
        DocumentReference docRef = db.collection("usuarios").document(userId);

        // Se obtienen los datos
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    if(usuario != null){
                        usuario.setId(documentSnapshot.getId());
                        usuario.setEmail(documentSnapshot.getString("email"));
                        // Se añaden los datos al userList
                        userList.add(usuario);
                        // Se notifica al adapter
                        userAdapter.notifyItemInserted(userList.size() - 1);
                    }
                }
            }
        });
    }

    // Metodo para eliminar al usuario de la actividad
    private void eliminarUsuario(Usuario usuario, int position) {

        //Se guardan los datos en variables
        String userIdToDelete = usuario.getId();
        String emailUsuario = usuario.getEmail();

        // Si el usuario no es null y esta en el campo de usuariosRegistrados, se eliminad
        if(userIdToDelete != null && usuariosRegistrados.containsKey(userIdToDelete)){
            usuariosRegistrados.remove(userIdToDelete);

            // Update del documento
            db.collection("posts").document(postId)
                    .update("usuariosRegistrados." + userIdToDelete, FieldValue.delete())
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show();
                        // Se elimina de la lista de usuarios
                        userList.remove(position);
                        // Se notifica al adapter
                        userAdapter.notifyItemRemoved(position);
                        enviarCorreos = new EnviarCorreos();
                        enviarCorreos.enviarCorreoAutorEliminaUsuario(getContext(), postUserId, emailUsuario, titulo);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "El usuario no existe en el mapa o ID es null", Toast.LENGTH_SHORT).show();
        }
    }

    // Metodo para mostrar el popup para eliminar el post
    private void popUpEliminarPost(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        
        builder.setTitle("Eliminar Actividad");
        builder.setMessage("Va a proceder a eliminar la actividad. ¿Esta seguro?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                eliminarPost();
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

    // Metodo para eliminar el post
    private void eliminarPost(){
        // Objeto de la clase EnviarCorreos y llamada al metodo
        enviarCorreos = new EnviarCorreos();
        ArrayList<String> emailList = new ArrayList<>();

        // Se notifica a todos los usuarios apuntados de que se elimina el post
        for(Usuario usuario : userList){
            emailList.add(usuario.getEmail());
        }

        // Delete del documento
        db.collection("posts").document(postId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Se envian los correos
                        for(String email : emailList){
                            enviarCorreos.enviarCorreoEliminarPost(getContext(), postUserId, email, titulo);
                        }
                        enviarCorreos.mostrarPopupMensaje(getContext(), "Se ha enviado un mensaje a los usuarios sobre la eliminacion de la actividad");
                        if(activity != null){
                            activity.goToFragment(new PostsFragment(), R.id.postsfragment);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Metodo para abrir los detalles del post
    private void openEditPost(){
        // Se crea un Bundle para pasar los datos
        Bundle bundle = new Bundle();
        bundle.putString("id", postId);
        bundle.putString("userId", postUserId);
        bundle.putString("titulo", titulo);
        bundle.putString("descripcion", descripcion);
        bundle.putString("localizacion", localizacion);
        bundle.putLong("fecha", fechaLong);
        bundle.putString("fechaString", fechaFormateada);
        bundle.putInt("numeroPersonas", numeroPersonas);
        bundle.putBoolean("materialNecesario", materialNecesario);
        bundle.putString("nombreAutor", nombreAutor);
        bundle.putString("imageUrl", imageUrl);
        bundle.putInt("usuariosRegistrados", userList.size());
        
        ArrayList<String> emailList = new ArrayList<>();
        for(Usuario usuario : userList){
            emailList.add(usuario.getEmail());
        }
        
        bundle.putStringArrayList("emailList", emailList);

        EditPostFragment editPostFragment = new EditPostFragment();
        editPostFragment.setArguments(bundle);

        if(activity != null){
            activity.goToFragment(editPostFragment, R.id.editpostfragment);
        }
    }

    // Metodo para abrir los detalles del usuario
    private void openUserDetail(Usuario usuario){
        // Se crea un Bundle para pasar los datos a la siguiente pantalla
        Bundle bundle = new Bundle();
        bundle.putString("nombre", usuario.getNombre());
        bundle.putString("apellido", usuario.getApellido());
        bundle.putString("email", usuario.getEmail());
        bundle.putInt("telefono", usuario.getTelefono());
        bundle.putString("imagePfpUrl", usuario.getImagePfpUrl());
        
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);
        
        if(activity != null){
            activity.goToFragment(userProfileFragment, R.id.userprofilefragment);
        }
        
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        if(i == R.id.btnActividad){
            operacionRegistro(postId,userId);
        }
        
        if(i == R.id.btnDescargarPdf){
            DescargarPdf descargarPdf = new DescargarPdf();
            descargarPdf.createPdf(
                    getContext(),
                    titulo,
                    descripcion,
                    localizacion,
                    fechaFormateada,
                    String.valueOf(numeroPersonas),
                    materialNecesario ? "Si" : "No",
                    nombreAutor
            );
        }
        
        if(i == R.id.btnEditarPost){
            openEditPost();
        }
        
        if(i == R.id.btnBorrarPost){
            popUpEliminarPost();
        }
    }

    // OnUserLongClick para eliminar al usuario que sea de la lista por parte del dueño
    @Override
    public void onUserLongClick(Usuario usuario, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Usuario")
                .setMessage("¿Deseas eliminar este usuario de la actividad?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eliminarUsuario(usuario, position);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onItemClick(Usuario usuario) {
        if(usuario.getId().equals(userIdAuth)){
            activity.goToFragment(new AccountFragment(), R.id.accountfragment);
        }else{
            openUserDetail(usuario);
        }
    }
}