/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author unwir
 */
public class DocumentoFormateado implements Serializable {
     private static final long serialVersionUTD = 1;
     private String contenido;
     private ArrayList<FormatoTexto> formatos;
     
     public DocumentoFormateado(){
         this.contenido = "";
         this.formatos = new ArrayList<>();
     }

    public DocumentoFormateado(String contenido, ArrayList<FormatoTexto> formatos) {
        this.contenido = contenido;
        this.formatos = (formatos != null) ? formatos : new ArrayList<>();
    }
    
    
    public ArrayList<FormatoTexto> getFormatos(){
        return formatos;
    }
     
     
     
    
}
