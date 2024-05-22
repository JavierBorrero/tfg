package com.example.tfg.models;

public class Usuario {
    
    public String nombre;
    
    public String apellido;
    
    public String email;
    
    public String imagePfpUrl;
    
    public int telefono;
    
    public Usuario(){}
    
    public Usuario(String nombre, String apellido, String email, String imagePfpUrl, int telefono){
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.imagePfpUrl = imagePfpUrl;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagePfpUrl() {
        return imagePfpUrl;
    }

    public void setImagePfpUrl(String imagePfpUrl) {
        this.imagePfpUrl = imagePfpUrl;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }
}
