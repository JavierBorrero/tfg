package com.example.tfg.models;

import java.util.Date;

public class Post {
    
    public String id;
    
    public String userId;
    
    public String nombreAutor;
    
    public String apellidoAutor;
    
    public String titulo;
    
    public String descripcion;
    
    public String localizacion;
    
    public Date fechaHora;
    
    public int numeroPersonas;
    
    public boolean material;
    
    public String imageUrl;
    
    public Post(){}
    
    public Post(String id, String userId, String titulo, String descripcion, String localizacion, Date fechaHora, int numeroPersonas, boolean material, String imageUrl){
        this.id = id;
        this.userId = userId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.localizacion = localizacion;
        this.fechaHora = fechaHora;
        this.numeroPersonas = numeroPersonas;
        this.material = material;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNombreAutor() {
        return nombreAutor;
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

    public String getApellidoAutor() {
        return apellidoAutor;
    }

    public void setApellidoAutor(String apellidoAutor) {
        this.apellidoAutor = apellidoAutor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getNumeroPersonas() {
        return numeroPersonas;
    }

    public void setNumeroPersonas(int numeroPersonas) {
        this.numeroPersonas = numeroPersonas;
    }

    public boolean isMaterial() {
        return material;
    }

    public void setMaterial(boolean material) {
        this.material = material;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
