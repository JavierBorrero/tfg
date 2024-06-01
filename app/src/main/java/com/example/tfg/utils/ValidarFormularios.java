package com.example.tfg.utils;

import android.widget.EditText;

import java.util.regex.Pattern;

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
    
    public boolean validarEmailRecuperarContrasena(EditText correo){
        boolean validar = true;
        
        String correoTrim = correo.getText().toString().trim();
        
        if(correoTrim.isEmpty()){
            correo.setError("Email vacio");
            validar = false;
        } else if (!validarEmail(correoTrim)) {
            correo.setError("Email no valido");
            validar = false;
        }else {
            correo.setError(null);
        }
        
        return validar;
    }
    
    public boolean validarNuevoPost(EditText titulo, EditText descripcion, EditText localizacion, EditText personas, EditText fechaHora){
        boolean validar = true;
        
        String tituloTrim = titulo.getText().toString().trim();
        String descripcionTrim = descripcion.getText().toString().trim();
        String localizacionTrim = localizacion.getText().toString().trim();
        String personasTrim = personas.getText().toString().trim();
        String fechaHoraTrim = fechaHora.getText().toString().trim();
        
        if(tituloTrim.isEmpty()){
            titulo.setError("Titulo vacio");
            validar = false;
        } else if (tituloTrim.length() < 20) {
            titulo.setError("Titulo demasiado corto");
            validar = false;
        }else {
            titulo.setError(null);
        }
        
        if(descripcionTrim.isEmpty()){
            descripcion.setError("Descripcion vacia");
            validar = false;
        } else if (descripcionTrim.length() < 40) {
            descripcion.setError("Descripcion demasiado corta");
            validar = false;
        }else {
            descripcion.setError(null);
        }
        
        if(localizacionTrim.isEmpty()){
            localizacion.setError("Localizacion vacia");
            validar = false;
        } else if (localizacionTrim.length() < 20) {
            localizacion.setError("Localizacion demasiado corta");
            validar = false;
        }else {
            localizacion.setError(null);
        }
        
        if(fechaHoraTrim.isEmpty()){
            fechaHora.setError("Fecha y hora no seleccionada");
            validar = false;
        }else {
            fechaHora.setError(null);
        }
        
        if(personasTrim.isEmpty()){
            personas.setError("Debe indicar un numero de personas");
            validar = false;
        } else if (!validarNumeroPersonasNewPost(personas)) {
            validar = false;
        }else {
            personas.setError(null);
        }

        return validar;
    }
    
    private boolean validarNumeroPersonasNewPost(EditText personas){
        boolean validar = true;
        
        int numeroPersonas = Integer.parseInt(personas.getText().toString());
        
        if(numeroPersonas <= 0 || numeroPersonas > 10){
            personas.setError("Numero entre 1 y 10");
            validar = false;
        }else {
            personas.setError(null);
        }
        
        return validar;
    }
    
    public boolean validarInicioSesion(EditText email, EditText password){
        boolean validar = true;
        
        String emailTrim = email.getText().toString().trim();
        String passwordTrim = password.getText().toString().trim();
        
        if(emailTrim.isEmpty()){
            email.setError("Email vacio");
            validar = false;
        }else {
            email.setError(null);
        }
        
        if(passwordTrim.isEmpty()){
            password.setError("Contraseña vacia");
            validar = false;
        }else {
            password.setError(null);
        }
        
        return validar;
    }
    
    private boolean validarEmail(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);

        if(email == null){
            return false;
        }

        return pat.matcher(email).matches();
    }
}
