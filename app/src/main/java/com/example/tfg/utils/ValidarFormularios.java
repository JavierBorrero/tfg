package com.example.tfg.utils;

import android.widget.EditText;

public class ValidarFormularios {
    
    public boolean validarEditarPost(EditText titulo, EditText descripcion, EditText localizacion, EditText fechaHora, EditText numeroPersonas, int usuariosRegistrados){
        boolean validar = true;
        
        String tituloTrim = titulo.getText().toString().trim();
        String descripcionTrim = descripcion.getText().toString().trim();
        String localizacionTrim = localizacion.getText().toString().trim();
        String cadenaFechaHora = fechaHora.getText().toString();
        String personasTrim = numeroPersonas.getText().toString();
        
        if(tituloTrim.isEmpty()){
            titulo.setError("Titulo vacio");
            validar = false;
        } else if (tituloTrim.length() < 10) {
            titulo.setError("Titulo demasiado corto");
            validar = false;
        }else{
            titulo.setError(null);
        }
        
        if(descripcionTrim.isEmpty()){
            descripcion.setError("Descripcion vacia");
            validar = false;
        } else if (descripcionTrim.length() < 40) {
            descripcion.setError("Descripcion demasiado corta");
            validar = false;
        }else{
            descripcion.setError(null);
        }
        
        if(localizacionTrim.isEmpty()){
            localizacion.setError("Localizacion vacia");
            validar = false;
        } else if (localizacionTrim.length() < 5) {
            localizacion.setError("Localizacion demasiado corta");
            validar = false;
        }else{
            localizacion.setError(null);
        }
        
        if(cadenaFechaHora.isEmpty()){
            fechaHora.setError("Seleccione una fecha y hora");
            validar = false;
        }else{
            fechaHora.setError(null);
        }
        
        if(personasTrim.isEmpty()){
            numeroPersonas.setError("Debe indicar un numero de personas");
            validar = false;
        } else if (!validarNumeroPersonas(numeroPersonas, usuariosRegistrados)) {
            validar = false;
        }else{
            numeroPersonas.setError(null);
        }

        return validar;
    }
    
    private boolean validarNumeroPersonas(EditText numeroPersonas, int usuariosRegistrados){
        boolean validar = true;
        
        int numPersonas = Integer.parseInt(numeroPersonas.getText().toString());
        
        if(numPersonas <= 0 || numPersonas > 10){
            numeroPersonas.setError("Numero entre 1 y 10");
            validar = false;
        } else if (numPersonas < usuariosRegistrados) {
            numeroPersonas.setError("Menos plazas que usuarios registrados");
            validar = false;
        }else{
            numeroPersonas.setError(null);
        }

        return validar;
    }
    
    public boolean validarNuevoYEditarAnuncio(EditText titulo, EditText descripcion){
        boolean validar = true;
        
        String tituloTrim = titulo.getText().toString().trim();
        String descripcionTrim = descripcion.getText().toString().trim();

        if(tituloTrim.isEmpty()){
            titulo.setError("Titulo vacio");
            validar = false;
        } else if (tituloTrim.length() < 10) {
            titulo.setError("Titulo demasiado corto");
            validar = false;
        }else{
            titulo.setError(null);
        }

        if(descripcionTrim.isEmpty()){
            descripcion.setError("Descripcion vacia");
            validar = false;
        } else if (descripcionTrim.length() < 40) {
            descripcion.setError("Descripcion demasiado corta");
            validar = false;
        }else{
            descripcion.setError(null);
        }
        
        return validar;
    }
    
    public boolean validarEditarPerfil(EditText nombre, EditText apellidos, EditText numeroTelefono){
        boolean validar = true;
        
        String nombreTrim = nombre.getText().toString().trim();
        String apellidosTrim = apellidos.getText().toString().trim();
        String telefonoTrim = numeroTelefono.getText().toString().trim();
        
        if(nombreTrim.isEmpty()){
            nombre.setError("Nombre vacio");
            validar = false;
        } else if (nombreTrim.length() < 5) {
            nombre.setError("Nombre demasiado corto");
            validar = false;
        }else {
            nombre.setError(null);
        }
        
        if(apellidosTrim.isEmpty()){
            apellidos.setError("Apellidos vacio");
            validar = false;
        } else if (apellidosTrim.length() < 5) {
            apellidos.setError("Apellido demasiado corto");
            validar = false;
        }else {
            apellidos.setError(null);
        }
        
        if(telefonoTrim.isEmpty()){
            numeroTelefono.setError("Telefono vacio");
            validar = false;
        } else if (telefonoTrim.length() != 9) {
            numeroTelefono.setError("Formato incorrecto");
            validar = false;
        }else {
            numeroTelefono.setError(null);
        }

        return validar;
    }
}
