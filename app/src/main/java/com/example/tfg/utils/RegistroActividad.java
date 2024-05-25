package com.example.tfg.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActividad {
    
    FirebaseFirestore db;
    String keyToRemove;
    
    public RegistroActividad(){
        db = FirebaseFirestore.getInstance();
    }
    
    public void comprobarUsuariosMaximos(String postId){
        
    }
    
    public void registrarUsuarioActividad(Context context, String postId, String userId){
        DocumentReference postRef = db.collection("posts").document(postId);

        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Map<String, Object> usuariosRegistrados = (Map<String, Object>) document.get("usuariosRegistrados");
                        if(usuariosRegistrados == null){
                            usuariosRegistrados = new HashMap<>();
                        }
                        
                        usuariosRegistrados.put(userId, true);

                        postRef.update("usuariosRegistrados", usuariosRegistrados).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Usuario apuntado", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }
    
    public void eliminarUsuarioActividad(Context context, String postId, String userId){
        DocumentReference postRef = db.collection("posts").document(postId);

        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Map<String, Object> usuariosRegistrados = (Map<String, Object>) document.get("usuariosRegistrados");
                        if(usuariosRegistrados != null){
                            for(Map.Entry<String, Object> entry : usuariosRegistrados.entrySet()){
                                if(entry.getKey().equals(userId)){
                                    keyToRemove = entry.getKey();
                                    break;
                                }
                            }
                            if(keyToRemove != null){
                                usuariosRegistrados.remove(keyToRemove);
                                postRef.update("usuariosRegistrados", usuariosRegistrados).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "Eliminado de la actividad", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }
    
    
}
