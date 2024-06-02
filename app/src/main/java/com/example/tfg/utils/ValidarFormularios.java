package com.example.tfg.utils;

import android.widget.EditText;

import java.util.regex.Pattern;

public class ValidarFormularios {
    /*
        === VALIDAR FORMULARIOS ===
        Esta clase se encarga de validar los formularios de la app
     */

    // Metodo para validar el formulario al editar el post
    public boolean validarEditarPost(EditText titulo, EditText descripcion, EditText localizacion, EditText fechaHora, EditText numeroPersonas, int usuariosRegistrados){
        // Booleano true en caso de que toda la validacion sea correcta
        boolean validar = true;

        // Se guardan los datos de los EditText en variables
        String tituloTrim = titulo.getText().toString().trim();
        String descripcionTrim = descripcion.getText().toString().trim();
        String localizacionTrim = localizacion.getText().toString().trim();
        String cadenaFechaHora = fechaHora.getText().toString();
        String personasTrim = numeroPersonas.getText().toString();

        // Comprobacion del titulo
        if(tituloTrim.isEmpty()){
            // Si esta vacio se marca el error y booleano en false
            titulo.setError("Titulo vacio");
            validar = false;
        } else if (tituloTrim.length() < 10) {
            // Si el titulo es demasiado corto se marca el error y booleano en false
            titulo.setError("Titulo demasiado corto");
            validar = false;
        }else{
            // Ningun error
            titulo.setError(null);
        }

        // Comprobacion de la descripcion
        if(descripcionTrim.isEmpty()){
            // Si esta vacio se marca el error y booleano en false
            descripcion.setError("Descripcion vacia");
            validar = false;
        } else if (descripcionTrim.length() < 40) {
            // Si la descripcion es demasiado corta se marca el error y booleano en false
            descripcion.setError("Descripcion demasiado corta");
            validar = false;
        }else{
            // Ningun error
            descripcion.setError(null);
        }

        // Comprobacion de la localizacion
        if(localizacionTrim.isEmpty()){
            // Si esta vacia se marca el error y booleano en false
            localizacion.setError("Localizacion vacia");
            validar = false;
        } else if (localizacionTrim.length() < 5) {
            // Si la localizacion es demasiado corta se marca el error y booleano en false
            localizacion.setError("Localizacion demasiado corta");
            validar = false;
        }else{
            // Ningun error
            localizacion.setError(null);
        }

        // Comprobacion de la fecha y la hora
        if(cadenaFechaHora.isEmpty()){
            // Si no se ha seleccionado fecha y hora se marca el error y booleano en false
            fechaHora.setError("Seleccione una fecha y hora");
            validar = false;
        }else{
            // Ningun error
            fechaHora.setError(null);
        }

        // Comprobacion del numero de personas
        if(personasTrim.isEmpty()){
            // Si esta vacio se marca el error y booleano en false
            numeroPersonas.setError("Debe indicar un numero de personas");
            validar = false;
        } else if (!validarNumeroPersonas(numeroPersonas, usuariosRegistrados)) {
            // Si el numero de personas no es correcto (entre 1 y 10) se marca el error y booleano en false
            validar = false;
        }else{
            // Ningun erorr
            numeroPersonas.setError(null);
        }

        return validar;
    }

    // Metodo para validar el numero de personas al editar post
    private boolean validarNumeroPersonas(EditText numeroPersonas, int usuariosRegistrados){
        // Booleano true en caso de que toda la validacion sea correcta
        boolean validar = true;

        // Se recoge el numero de personas
        int numPersonas = Integer.parseInt(numeroPersonas.getText().toString());
        
        if(numPersonas <= 0 || numPersonas > 10){
            // Si es menor o igual que 0 o superior a 10 se marca el error y booleano en false
            numeroPersonas.setError("Numero entre 1 y 10");
            validar = false;
        } else if (numPersonas < usuariosRegistrados) {
            // Si se quieren poner menos plazas que usuarios ya registrados se marca el error y booleano en false
            numeroPersonas.setError("Menos plazas que usuarios registrados");
            validar = false;
        }else{
            //Ningun error
            numeroPersonas.setError(null);
        }

        return validar;
    }

    // Metodo para validar la creacion y la edicion de un anuncio
    public boolean validarNuevoYEditarAnuncio(EditText titulo, EditText descripcion){
        // Booleano en true en caso de que toda la validacion sea correcta
        boolean validar = true;

        // Se guardan los datos de los EditText en variables
        String tituloTrim = titulo.getText().toString().trim();
        String descripcionTrim = descripcion.getText().toString().trim();

        // Comprobacion del titulo
        if(tituloTrim.isEmpty()){
            // Si el titulo esta vacio se marca el error y booleano en false
            titulo.setError("Titulo vacio");
            validar = false;
        } else if (tituloTrim.length() < 10) {
            // SI el titulo es demasido corto se marca el error y el booleano en false
            titulo.setError("Titulo demasiado corto");
            validar = false;
        }else{
            // Ningun error
            titulo.setError(null);
        }

        // Comprobacion de descripcion
        if(descripcionTrim.isEmpty()){
            // Si la descripcion esta vacia se marca el error y booleano en false
            descripcion.setError("Descripcion vacia");
            validar = false;
        } else if (descripcionTrim.length() < 40) {
            // Si la descripcion es demasido corta se marca el error y el booleano en false
            descripcion.setError("Descripcion demasiado corta");
            validar = false;
        }else{
            // Ningun error
            descripcion.setError(null);
        }
        
        return validar;
    }

    // Metodo para la validacion de editar perfil
    public boolean validarEditarPerfil(EditText nombre, EditText apellidos, EditText numeroTelefono){
        // Booleano true en caso de que toda la validacion sea correcta
        boolean validar = true;

        // Se guardan los datos de los EditText en variables
        String nombreTrim = nombre.getText().toString().trim();
        String apellidosTrim = apellidos.getText().toString().trim();
        String telefonoTrim = numeroTelefono.getText().toString().trim();

        // Comprobacion nombre
        if(nombreTrim.isEmpty()){
            // Si el nombre esta vacio se marca el error y booleano en false
            nombre.setError("Nombre vacio");
            validar = false;
        } else if (nombreTrim.length() < 2) {
            // Si el nombre es demasiado corto se marca el error y booleano en false
            nombre.setError("Nombre demasiado corto");
            validar = false;
        }else {
            // Ningun error
            nombre.setError(null);
        }

        // Comprobacion apellidos
        if(apellidosTrim.isEmpty()){
            // Si el apellido esta vacio se marca el error y booleano en false
            apellidos.setError("Apellidos vacio");
            validar = false;
        } else if (apellidosTrim.length() < 3) {
            // Si el apellido es demasido corto se marca el error y booleano en false
            apellidos.setError("Apellido demasiado corto");
            validar = false;
        }else {
            // Ningun error
            apellidos.setError(null);
        }

        // Comprobacion telefono
        if(telefonoTrim.isEmpty()){
            // Si el telefono esta vacio se marca el error y booleano en false
            numeroTelefono.setError("Telefono vacio");
            validar = false;
        } else if (telefonoTrim.length() != 9) {
            // Si el telefono no tiene 9 numeros se marca el error y booleano en false
            numeroTelefono.setError("Formato incorrecto");
            validar = false;
        }else {
            // Ningun error
            numeroTelefono.setError(null);
        }

        return validar;
    }

    // Metodo para validar el email en recuperar contraseña
    public boolean validarEmailRecuperarContrasena(EditText correo){
        // Booleano true en caso de que toda la validacion sea correcta
        boolean validar = true;

        // Se guarda el dato del EditTetx en una variable
        String correoTrim = correo.getText().toString().trim();

        // Comprobacion correo
        if(correoTrim.isEmpty()){
            // Si el correo esta vacio se marca el error y booleano en false
            correo.setError("Email vacio");
            validar = false;
        } else if (!validarEmail(correoTrim)) {
            // Si el formato del correo no es valido se marca el error y booleano en false
            correo.setError("Email no valido");
            validar = false;
        }else {
            // Ningun error
            correo.setError(null);
        }
        
        return validar;
    }

    // Metodo para validar un nuevo post
    public boolean validarNuevoPost(EditText titulo, EditText descripcion, EditText localizacion, EditText personas, EditText fechaHora){
        // Booleano true en caso de que toda la validacion sea correcta
        boolean validar = true;

        // Se guardan los datos de los EditText en variables
        String tituloTrim = titulo.getText().toString().trim();
        String descripcionTrim = descripcion.getText().toString().trim();
        String localizacionTrim = localizacion.getText().toString().trim();
        String personasTrim = personas.getText().toString().trim();
        String fechaHoraTrim = fechaHora.getText().toString().trim();

        // Comprobacion titulo
        if(tituloTrim.isEmpty()){
            // Si el titulo esta vacio se marca el error y booleano en false
            titulo.setError("Titulo vacio");
            validar = false;
        } else if (tituloTrim.length() < 20) {
            // Si el titulo es demasiado corto se marca el error y booleano en false
            titulo.setError("Titulo demasiado corto");
            validar = false;
        }else {
            // Ningun error
            titulo.setError(null);
        }

        // Comprobacion descripcion
        if(descripcionTrim.isEmpty()){
            // Si la descripcion esta vacia se marca el error y booleano en false
            descripcion.setError("Descripcion vacia");
            validar = false;
        } else if (descripcionTrim.length() < 50) {
            // Si la descripcion es demasido corta se marca el error y booleano en false
            descripcion.setError("Descripcion demasiado corta");
            validar = false;
        }else {
            // Ningun error
            descripcion.setError(null);
        }

        // Comprobacion localizacion
        if(localizacionTrim.isEmpty()){
            // Si la localizacion esta vacia se marca el error y booleano en false
            localizacion.setError("Localizacion vacia");
            validar = false;
        } else if (localizacionTrim.length() < 20) {
            // Si la localizacion es demasiado corta se marca el error y booleano en false
            localizacion.setError("Localizacion demasiado corta");
            validar = false;
        }else {
            // Ningun error
            localizacion.setError(null);
        }

        // Comprobacion fecha y hora
        if(fechaHoraTrim.isEmpty()){
            // Si no se ha seleccionado ninguna fecha y hora se marca el error y booleano en false
            fechaHora.setError("Fecha y hora no seleccionada");
            validar = false;
        }else {
            // Ningun error
            fechaHora.setError(null);
        }

        // Comprobacion numero personas
        if(personasTrim.isEmpty()){
            // Si el numero de personas esta vacio se marca el error y el booleano en false
            personas.setError("Debe indicar un numero de personas");
            validar = false;
        } else if (!validarNumeroPersonasNewPost(personas)) {
            // Si el numero de personas no es valido (entre 1 y 10) se marca el error y el booleano en false
            validar = false;
        }else {
            // Ningun error
            personas.setError(null);
        }

        return validar;
    }

    // Metodo para validar el numero de personas en el nuevo post
    private boolean validarNumeroPersonasNewPost(EditText personas){
        // Booleano en true en caso de que toda la validacion sea correcta
        boolean validar = true;

        // Se guarda el dato del EditText en una variable
        int numeroPersonas = Integer.parseInt(personas.getText().toString());

        if(numeroPersonas <= 0 || numeroPersonas > 10){
            // Si el numero de personas es menor o igual que 0 y mayor que 10 se marca el error y booleano en false
            personas.setError("Numero entre 1 y 10");
            validar = false;
        }else {
            // Ningun error
            personas.setError(null);
        }
        
        return validar;
    }

    // Metodo para validar el inicio de sesion
    public boolean validarInicioSesion(EditText email, EditText password){
        // Booleano en true en caso de que toda la validacion sea correcta
        boolean validar = true;

        // Se guardan los datos del EditText en variables
        String emailTrim = email.getText().toString().trim();
        String passwordTrim = password.getText().toString().trim();

        // Comprobacion email
        if(emailTrim.isEmpty()){
            // Si el email esta vacio se marca el error y booleano en false
            email.setError("Email vacio");
            validar = false;
        }else if (!validarEmail(emailTrim)){
            // Si el formato del email no es correcto se marca el error y booleano en false
            email.setError("Email no valido");
            validar = false;
        }else {
            // Ningun error
            email.setError(null);
        }

        // Comprobacion contraseña
        if(passwordTrim.isEmpty()){
            // Si la contraseña esta vacia se marca el error y booleano en false
            password.setError("Contraseña vacia");
            validar = false;
        }else {
            // Ningun error
            password.setError(null);
        }
        
        return validar;
    }

    // Metodo para validar el registro
    public boolean validarRegistro(EditText nombre, EditText apellido, EditText telefono, EditText email, EditText contrasena, EditText confirmarContrasena){
        // Boolean true en caso de que toda la validacion sea correcta
        boolean validar = true;

        // Se guardan los datos de los EditText en variables
        String nombreTrim = nombre.getText().toString().trim();
        String apellidoTrim = apellido.getText().toString().trim();
        String telefonoTrim = telefono.getText().toString().trim();
        String emailTrim = email.getText().toString().trim();
        String contrasenaTrim = contrasena.getText().toString().trim();
        String confirmarContrasenaTrim = confirmarContrasena.getText().toString().trim();

        // Comprobacion nombre
        if(nombreTrim.isEmpty()){
            // Si el nombre esta vacio se marca el error y boolean en false
            nombre.setError("Nombre vacio");
            validar = false;
        } else if (nombreTrim.length() < 2) {
            // Si el nombre es demasiado corto se marca el error y boolean false
            nombre.setError("Nombre demasiado corto");
            validar = false;
        }else {
            // Ningun error
            nombre.setError(null);
        }

        // Comprobacion apellido
        if(apellidoTrim.isEmpty()){
            // Si el apellido esta vacio se marca el error y boolean en false
            apellido.setError("Apellido vacio");
            validar = false;
        } else if (apellidoTrim.length() < 3) {
            // Si el apellido es demasiado corto se marca el error y el boolean en false
            apellido.setError("Apellido demasiado corto");
            validar = false;
        }else {
            // Ningun error
            apellido.setError(null);
        }

        // Comprobacion telefono
        if(telefonoTrim.isEmpty()){
            // Si el telefono esta vacio se marca el error y boolean en false
            telefono.setError("Telefono vacio");
            validar = false;
        } else if (telefonoTrim.length() != 9) {
            // Si el telefono no tiene nueve numeros se marca el error y el boolean en false
            telefono.setError("Formato incorrecto");
            validar = false;
        }else {
            // Ningun error
            telefono.setError(null);
        }

        // Comprobacion email
        if(emailTrim.isEmpty()){
            // Si el email esta vacio se marca el error y boolean en false
            email.setError("Email vacio");
            validar = false;
        } else if (!validarEmail(emailTrim)) {
            // Si el formato del email no es correcto se marca el error y boolean en false
            email.setError("Email no valido");
            validar = false;
        }else {
            // Ningun error
            email.setError(null);
        }

        // Comprobar contraseña
        if(contrasenaTrim.isEmpty()){
            // Si la contraseña esta vacia se marca el error y boolean en false
            contrasena.setError("Contraseña vacia");
            validar = false;
        } else if (!validarContrasena(contrasenaTrim)) {
            // Si el formato de la contraseña es incorrecto se marca el error y boolean en false
            contrasena.setError("Contraseña no valida");
            validar = false;
        }else {
            // Ningun error
            contrasena.setError(null);
        }

        // Comprobar confirmar contraseña
        if(confirmarContrasenaTrim.isEmpty()){
            // Si la contraseña esta vacia se marca el error y boolean en false
            confirmarContrasena.setError("Confirmar contraseña vacio");
            validar = false;
        } else if (!confirmarContrasenaTrim.equals(contrasenaTrim)) {
            // Si la confirmacion no coincide con la otra contraseña se marca el error y boolean en false
            confirmarContrasena.setError("Las contraseñas no coinciden");
            validar = false;
        }else {
            // Ningun error
            confirmarContrasena.setError(null);
        }
        
        return validar;
    }

    // Metodo para validar el email
    private boolean validarEmail(String email){
        // OWASP Validation email
        // Si el patron coincide con el email indicado se pasa la validacion
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";

        Pattern pat = Pattern.compile(emailRegex);

        if(email == null){
            return false;
        }

        return pat.matcher(email).matches();
    }

    // Metodo para validar la contraseña
    private boolean validarContrasena(String contrasena){
        /*
            Patron que debe cumplir lo siguiente:
            - Al menos un digito (0-9)
            - Al menos una letra minuscula (a-z)
            - Al menos una letra mayuscula (A-Z)
            - Un caracter especial de los siguientes: @#&()–[{}]:;',?/*~$^+=<>._
            - Longitud minima de 12 caracteres
         */
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>._]).{12,}$";

        Pattern pat = Pattern.compile(passwordRegex);

        if(contrasena == null){
            return false;
        }

        return pat.matcher(contrasena).matches();
    }
}
