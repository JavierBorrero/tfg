package com.example.tfg;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.example.tfg.databinding.FragmentNewPostBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class NewPostFragment extends Fragment implements View.OnClickListener {
    
    private FragmentNewPostBinding binding;

    EditText titulo, descripcion, localizacion, fecha, hora, imagen, personas;
    
    CheckBox material;
    
    ImageView foto;

    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Uri image_uri = result.getData().getData();
                        foto.setImageURI(image_uri);
                        String textoImagen = obtenerNombreImagen(image_uri);
                        binding.fieldImagen.setText(textoImagen);
                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        binding = FragmentNewPostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        binding.btnSubmitPost.setOnClickListener(this);
        binding.fieldFecha.setOnClickListener(this);
        binding.fieldHora.setOnClickListener(this);
        binding.fieldImagen.setOnClickListener(this);

        titulo = binding.fieldTitulo;
        descripcion = binding.fieldDescripcion;
        localizacion = binding.fieldLocalizacion;
        fecha = binding.fieldFecha;
        hora = binding.fieldHora;
        imagen = binding.fieldImagen;
        foto = binding.fotoPrevia;
        personas = binding.fieldNumeroPersonas;
        material = binding.fieldMaterial;

    }

    private void submitPost(){

        if(!validarCampos()){
            return;
        }

        //NavHostFragment.findNavController(NewPostFragment.this).navigate(R.id.action_NewPostFragment_to_MainFragment);

        String cadenaTitulo = titulo.getText().toString();
        String cadenaDescripcion = descripcion.getText().toString();
        String cadenaLocalizacion = localizacion.getText().toString();
        String cadenaFecha = fecha.getText().toString();
        String cadenaHora = hora.getText().toString();
        String cadenaImagen = imagen.getText().toString();
        String cadenaPersonas = personas.getText().toString();
        String boolMaterial;
        boolean booleanMaterial = material.isChecked();

        if(booleanMaterial){
            boolMaterial = "true";
        }else{
            boolMaterial = "false";
        }
    }

    // === INICIO VALIDACION ===
    private boolean validarCampos(){
        /*
            Booleano para manejar la validacion
            Si algun campo esta mal este valor cambia a false y la validacion
            no es correcta
         */
        boolean validar = true;

        // Comprobar que el campo titulo no esta vacio
        if(titulo.getText().toString().isEmpty()){
            titulo.setError("Titulo vacio");
            validar = false;
        }else{
            titulo.setError(null);
        }

        // Comprobar que el campo descripcion no esta vacio
        if(descripcion.getText().toString().isEmpty()){
            descripcion.setError("Descripcion vacia");
            validar = false;
        }else{
            descripcion.setError(null);
        }

        // Comprobar que el campo localizacion no esta vacio
        if(localizacion.getText().toString().isEmpty()){
            localizacion.setError("Localizacion vacia");
            validar = false;
        }else{
            localizacion.setError(null);
        }

        // Comprobar que el campo fecha no esta vacio
        if(fecha.getText().toString().isEmpty()){
            fecha.setError("Debe seleccionar una fecha");
            validar = false;
        }else{
            fecha.setError(null);
        }

        // Comprobar que el campo hora no esta vacio
        if(hora.getText().toString().isEmpty()){
            hora.setError("Debe seleccionar una hora");
            validar = false;
        }else{
            hora.setError(null);
        }

        // Comprobar que el campo personas no esta vacio o que el numero de personas sea correcto
        if(personas.getText().toString().isEmpty()){
            personas.setError("Debe indicar un número de personas");
            validar = false;
        } else if (!validarNumeroPersonas()) {
            validar = false;
        }else{
            personas.setError(null);
        }

        return validar;
    }

    /*
        Funcion para validar el numero de personas que se pueden apuntar a la actividad
     */
    private boolean validarNumeroPersonas(){
        boolean validar = true;

        int numeroPersonas = Integer.parseInt(personas.getText().toString());

        if (numeroPersonas <= 0 || numeroPersonas > 10){
            personas.setError("Número entre 1 y 10");
            validar = false;
        }else{
            personas.setError(null);
        }

        return validar;
    }

    // === FIN VALIDACION ===

    /*
        Funcion para mostrar el calendario y obtener la fecha
     */
    private void obtenerFecha(){

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                fecha.setText(day + "/" + month + "/" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    /*
        Funcion para mostrar el reloj y obtener la hora
     */
    private void obtenerHora(){

        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                hora.setText(hour + ":" + minute);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void obtenerImagen(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryActivityResultLauncher.launch(intent);
    }

    private String obtenerNombreImagen(Uri uri){
        String path = getRealPathFromURI(getContext(), uri);
        String filename = path.substring(path.lastIndexOf("/")+1);

        String fileWOExtension;

        if (filename.indexOf(".") > 0) {
            fileWOExtension = filename.substring(0, filename.lastIndexOf("."));
        } else {
            fileWOExtension =  filename;
        }

        return filename;
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();

        if(i == R.id.btnSubmitPost){
            submitPost();
        }

        if(i == R.id.fieldFecha){
            obtenerFecha();
        }

        if(i == R.id.fieldHora){
            obtenerHora();
        }

        if(i == R.id.fieldImagen){
            obtenerImagen();
        }
    }
}