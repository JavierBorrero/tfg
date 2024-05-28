package com.example.tfg.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EnviarCorreos {
    
    String emailPostUser;
    String nombre;
    String apellidos;
    String nombreAutor;
    String apellidoAutor;
    FirebaseFirestore db;
    
    public EnviarCorreos(){
        db = FirebaseFirestore.getInstance();
    }
    
    public void enviarEmailUsuarioActividad(Context context, String postUserId, String userId, String tituloActividad, boolean flag){
        
        DocumentReference userPostRef = db.collection("usuarios").document(postUserId);
        
        // Se obtiene el email del dueño del post y otros datos para completar el correo
        userPostRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        emailPostUser = document.getString("email");
                        
                        // Se obtiene informacion del usuario conectado para añadirla al email
                        DocumentReference userRef = db.collection("usuarios").document(userId);
                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if(document.exists()){
                                        nombre = document.getString("nombre");
                                        apellidos = document.getString("apellido");
                                        
                                        if(flag){
                                            enviarCorreoApuntarActividad(context, tituloActividad);    
                                        }else{
                                            enviarCorreoEliminarActividad(context, tituloActividad);
                                        }
                                        
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    
    private void enviarCorreoApuntarActividad(Context context, String tituloActividad){
        Map<String, Object> emailMessage = new HashMap<>();
        emailMessage.put("subject", nombre + " " + apellidos + " se ha apuntado a tu actividad");
        emailMessage.put("text", "El usuario " + nombre + " " + apellidos + " se ha apuntado a tu actividad: " + tituloActividad + "\n" + 
                "Entra en la aplicacion y contacta con el!" + "\n\n" +
                "DAM TFG JAVIER BORRERO DEL CERRO");

        Map<String, Object> emailData = new HashMap<>();
        emailData.put("to", emailPostUser);
        emailData.put("message", emailMessage);

        db.collection("mail").document()
                .set(emailData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mostrarPopupMensaje(context, "Se le ha apuntado a la actividad y se ha enviado un mensaje al propietario de este anuncio");
                    }
                });
    }
    
    private void enviarCorreoEliminarActividad(Context context, String tituloActividad){
        Map<String, Object> emailMessage = new HashMap<>();
        emailMessage.put("subject", nombre + " " + apellidos + " se ha eliminado de tu actividad");
        emailMessage.put("text", "El usuario " + nombre + " " + apellidos + " se ha eliminado de tu actividad: " + tituloActividad + "\n" +
                "Entra en la aplicacion y contacta con el!" + "\n\n" +
                "DAM TFG JAVIER BORRERO DEL CERRO");

        Map<String, Object> emailData = new HashMap<>();
        emailData.put("to", emailPostUser);
        emailData.put("message", emailMessage);

        db.collection("mail").document()
                .set(emailData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mostrarPopupMensaje(context, "Se le ha elimado de la actividad y se ha enviado un mensaje al propietario de este anuncio");
                    }
                });
    }
    
    public void enviarCorreoAutorEliminaUsuario(Context context, String postUserId, String emailUsuario, String tituloActividad){
        DocumentReference userRef = db.collection("usuarios").document(postUserId);
        
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        nombreAutor = document.getString("nombre");
                        apellidoAutor = document.getString("apellido");
                    }
                }
            }
        });
        
        Map<String, Object> emailMessage = new HashMap<>();
        emailMessage.put("subject", nombreAutor + " " + apellidoAutor + " te ha eliminado de su actividad");
        emailMessage.put("text", "El usuario " + nombreAutor + " " + apellidoAutor + " te ha eliminado de su actividad: " + tituloActividad + "\n" + 
                "Entra en la aplicacion y contacta con el para descubrir el porque" + "\n\n" + 
                "DAM TFG JAVIER BORRERO DEL CERRO");
        
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("to", emailUsuario);
        emailData.put("message", emailMessage);
        
        db.collection("mail").document()
                .set(emailData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mostrarPopupMensaje(context, "Se ha eliminado al usuario de la actividad y se le ha enviado un mensaje");
                    }
                });
    }
    
    
    private void mostrarPopupMensaje(Context context, String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        
        builder.setTitle("Confirmacion");
        builder.setMessage(mensaje);
        
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
