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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

public class EliminarUsuario {

    /*
        === ELIMINAR USUARIO ===
        Esta clase elimina al usuario y todos los datos relacionados con el

        1. Borrar los posts del usuario
        2. Borrar los anuncios del usuario
        3. Borrar el userId del usuario a eliminar de los posts donde aparezca en usuariosRegistrados
        4. Borrar el usuario de FirebaseAuth
     */
    
    FirebaseFirestore db;
    FirebaseAuth auth;
    
    public EliminarUsuario(){
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void eliminarUsuario(Context context, String userId) {
        // Obtener y borrar los posts del usuario con userId
        db.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // WriteBatch para poder eliminar varios documentos a la vez
                            WriteBatch batch = db.batch();
                            QuerySnapshot querySnapshot = task.getResult();

                            // Si la lista de documentos no esta vacia se eliminan los documentos
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    batch.delete(document.getReference());
                                }
                            }

                            // Se confirma la eliminacion de todos los posts
                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // 2. Eliminar los anuncios del usuario
                                        db.collection("anuncios")
                                                .whereEqualTo("userId", userId)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            // WriteBatch para poder eliminar varios documentos a la vez
                                                            WriteBatch batchAnuncios = db.batch();
                                                            QuerySnapshot querySnapshotAnuncios = task.getResult();

                                                            // Si la lista de documentos no esta vacia se elimina
                                                            if (!querySnapshotAnuncios.isEmpty()) {
                                                                for (DocumentSnapshot document : querySnapshotAnuncios.getDocuments()) {
                                                                    batchAnuncios.delete(document.getReference());
                                                                }
                                                            }

                                                            // Se confirma la eliminacion de todos los anuncios
                                                            batchAnuncios.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        // 3. Eliminar el userId del campo usuariosRegistrados en los posts
                                                                        db.collection("posts")
                                                                                .whereEqualTo("usuariosRegistrados." + userId, true)
                                                                                .get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            // WriteBatch para poder eliminar de varios documentos a la vez
                                                                                            WriteBatch batchPostsUsuariosRegistrados = db.batch();
                                                                                            QuerySnapshot querySnapshotPostsUsuariosRegistrados = task.getResult();

                                                                                            // Si la lista de documentos no esta vacia se hace el update
                                                                                            if (!querySnapshotPostsUsuariosRegistrados.isEmpty()) {
                                                                                                for (DocumentSnapshot document : querySnapshotPostsUsuariosRegistrados.getDocuments()) {
                                                                                                    DocumentReference postRef = document.getReference();
                                                                                                    batchPostsUsuariosRegistrados.update(postRef, "usuariosRegistrados." + userId, FieldValue.delete());
                                                                                                }
                                                                                            }

                                                                                            // Se confirma la eliminacion del usuariosRegistrados para el userId
                                                                                            batchPostsUsuariosRegistrados.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        // 4. Eliminamos el documento del usuario
                                                                                                        DocumentReference userRef = db.collection("usuarios").document(userId);
                                                                                                        // Una vez se elimina el documento se le borra de FirebaseAuth
                                                                                                        userRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful()) {
                                                                                                                    auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                // Todos los pasos completados
                                                                                                                                Toast.makeText(context, "Usuario, posts y anuncios eliminados", Toast.LENGTH_SHORT).show();
                                                                                                                                Log.d("DEBUG", "USUARIO, POSTS Y ANUNCIOS ELIMINADOS");
                                                                                                                            } else {
                                                                                                                                // Error al eliminar
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
                                                                                                        Toast.makeText(context, "Error al eliminar los usuarios registrados del usuario", Toast.LENGTH_SHORT).show();
                                                                                                        Log.d("DEBUG", "ERROR AL ELIMINAR LOS USUARIOS REGISTRADOS DEL USUARIO");
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
                                                                    } else {
                                                                        Toast.makeText(context, "Error al eliminar los anuncios del usuario", Toast.LENGTH_SHORT).show();
                                                                        Log.d("DEBUG", "ERROR AL ELIMINAR LOS ANUNCIOS DEL USUARIO");
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
                                                            Toast.makeText(context, "Error al obtener los anuncios del usuario", Toast.LENGTH_SHORT).show();
                                                            Log.d("DEBUG", "ERROR AL OBTENER LOS ANUNCIOS DEL USUARIO");
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
