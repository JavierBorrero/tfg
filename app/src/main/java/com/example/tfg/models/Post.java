package com.example.tfg.models;

import java.util.Date;

public class Post {
    
    public String userId;
    
    public String nombreAutor;
    
    public String titulo;
    
    public String descripcion;
    
    public String localizacion;
    
    public Date fechaHora;
    
    public int numeroPersonas;
    
    public String imageUrl;
    
    public boolean material;
    
    public Post(){}
    
    public Post(String userId, String nombreAutor, String titulo, String descripcion, String localizacion, Date fechaHora, int numeroPersonas, String imageUrl, boolean material){
        this.userId = userId;
        this.nombreAutor = nombreAutor;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.localizacion = localizacion;
        this.fechaHora = fechaHora;
        this.numeroPersonas = numeroPersonas;
        this.imageUrl = imageUrl;
        this.material = material;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isMaterial() {
        return material;
    }

    public void setMaterial(boolean material) {
        this.material = material;
    }
}
