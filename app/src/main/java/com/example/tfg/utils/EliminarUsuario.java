package com.example.tfg.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

public class EliminarUsuario {
    
    //1. Borrar los posts del usuario
    //2. Borrar el documento del usuario
    //3. Borrar el usuario de FirebaseAuth
    
    FirebaseFirestore db;
    FirebaseAuth auth;
    
    public EliminarUsuario(){
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }
    
    public void eliminarUsuario(Context context, String userId) {
        db.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            WriteBatch batch = db.batch();
                            QuerySnapshot querySnapshot = task.getResult();

                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    batch.delete(document.getReference());
                                }
                            }

                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        DocumentReference userRef = db.collection("usuarios").document(userId);
                                        userRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(context, "Usuario y posts eliminados", Toast.LENGTH_SHORT).show();
                                                                Log.d("DEBUG", "USUARIO Y POSTS ELIMINADOS");
                                                            } else {
                                                                Toast.makeText(context, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
                                                                Log.d("DEBUG", "ERROR AL ELIMINAR EL USUARIO");
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(context, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                            Log.d("DEBUG", "ERROR: " + e.getLocalizedMessage());
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(context, "Error al eliminar el documento de Firebase", Toast.LENGTH_SHORT).show();
                                                    Log.d("DEBUG", "ERROR AL ELIMINAR EL DOCUMENTO DE FIREBASE");
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                Log.d("DEBUG", "ERROR: " + e.getLocalizedMessage());
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context, "Error al eliminar los posts del usuario", Toast.LENGTH_SHORT).show();
                                        Log.d("DEBUG", "ERROR AL ELIMINAR LOS POSTS DEL USUARIO");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("DEBUG", "ERROR: " + e.getLocalizedMessage());
                                }
                            });
                        } else {
                            Toast.makeText(context, "Error al obtener los posts del usuario", Toast.LENGTH_SHORT).show();
                            Log.d("DEBUG", "ERROR AL OBTENER LOS POSTS DEL USUARIO");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("DEBUG", "ERROR: " + e.getLocalizedMessage());
                    }
                });
    }
}
