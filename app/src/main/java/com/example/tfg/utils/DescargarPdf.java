package com.example.tfg.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DescargarPdf {
    
    public void createPdf(Context context, String titulo, String descripcion, String localizacion, String fecha, String numeroPersonas, String materialNecesario, String nombreAutor) {
        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PostDetails.pdf";
            File file = new File(path);
            file.getParentFile().mkdirs();
            
            PdfWriter writer = new PdfWriter(new FileOutputStream(file));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(36, 36, 36, 36);
            
            Paragraph title = new Paragraph(titulo)
                    .setFont(PdfFontFactory.createFont("Helvetica-Bold"))
                    .setFontSize(24)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setMarginBottom(20);
            document.add(title);
            
            Paragraph content = new Paragraph()
                    .setFontSize(12)
                    .add("Descripción: " + descripcion + "\n\n")
                    .add("Localización: " + localizacion + "\n\n")
                    .add("Fecha y hora: " + fecha + "\n\n")
                    .add("Número de personas: " + numeroPersonas + "\n\n")
                    .add("Material necesario: " + materialNecesario + "\n\n")
                    .add("Autor: " + nombreAutor);
            document.add(content);
            
            document.close();

            Toast.makeText(context, "PDF generado exitosamente en " + path, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("PDF Creation Error", "Error al crear el PDF: ", e);
            Toast.makeText(context, "Error al crear el PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

