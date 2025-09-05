/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author hnleo
 */
public class GestorArchivos implements Serializable{
    
    ObjectOutputStream outDoc;
    ObjectInputStream inputDoc;
    StringBuilder cont;
    BufferedReader bReader;
    BufferedWriter bWriter;
    
    public DocumentoFormateado cargarDoc(File file) throws IOException, ClassNotFoundException{
        try{
            inputDoc = new ObjectInputStream(new FileInputStream(file));
            return (DocumentoFormateado) inputDoc.readObject();
        }catch(IOException io){
            System.out.println("Ocurrio un error en Disco: "+io.getMessage());
        }catch(ClassNotFoundException cl){
            System.out.println("Ocurrio un error, clase de objeto no encontrada. "+cl.getMessage());
        }
        return null;
    }
    
    public void guardarDoc(DocumentoFormateado doc, File file) throws IOException{
        try{
            outDoc = new ObjectOutputStream(new FileOutputStream(file));
            outDoc.writeObject(doc);
        }catch(IOException io){
            System.out.println("Ocurrio un error en Disco: "+io.getMessage());
        }
    }
    
    public String cargarTexto(File file) throws IOException{
        cont = new StringBuilder();
        String linea;
        try{
            bReader = new BufferedReader(new FileReader(file));
            while((linea = bReader.readLine()) != null){
                cont.append(linea).append("\n");
            }
        }catch(IOException io){
            System.out.println("Ocurrio un error en disco. "+ io.getMessage());
        }
        
        return cont.toString();
    }
    
    public void exportar(String cont, File file) throws IOException{
        try{
            bWriter = new BufferedWriter(new FileWriter(file));
            bWriter.write(cont);
        }catch(IOException io){
            System.out.println("Ocurrio un error en disco. "+io.getMessage());
        }
    }
}
