/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTextPane;
import javax.swing.text.*;

/**
 *
 * @author hnleo
 */
public class GestorArchivos implements Serializable {
    
    private ObjectOutputStream outDoc;
    private ObjectInputStream inputDoc;
    private StringBuilder cont;
    private BufferedReader bReader;
    private BufferedWriter bWriter;
    
    public DocumentoFormateado cargarDoc(File file) throws IOException, ClassNotFoundException {
        try {
            inputDoc = new ObjectInputStream(new FileInputStream(file));
            return (DocumentoFormateado) inputDoc.readObject();
        } catch(IOException io) {
            System.out.println("Ocurrió un error en Disco: " + io.getMessage());
            throw io;
        } catch(ClassNotFoundException cl) {
            System.out.println("Ocurrió un error, clase de objeto no encontrada. " + cl.getMessage());
            throw cl;
        } finally {
            if (inputDoc != null) {
                try { inputDoc.close(); } catch (IOException e) {  }
            }
        }
    }
    
    public void guardarDoc(DocumentoFormateado doc, File file) throws IOException {
        try {
            outDoc = new ObjectOutputStream(new FileOutputStream(file));
            outDoc.writeObject(doc);
        } catch(IOException io) {
            System.out.println("Ocurrió un error en Disco: " + io.getMessage());
            throw io;
        } finally {
            if (outDoc != null) {
                try { outDoc.close(); } catch (IOException e) { }
            }
        }
    }
    
    public void exportarRTF(JTextPane textPane, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            
            Map<Color, Integer> tablaColores = new HashMap<>();
            List<Color> listaColores = new ArrayList<>();
          
            listaColores.add(Color.BLACK);      
            listaColores.add(Color.RED);        
            listaColores.add(Color.GREEN);      
            listaColores.add(Color.BLUE);      
            listaColores.add(Color.YELLOW);     
            listaColores.add(Color.MAGENTA);   
            listaColores.add(Color.CYAN);      
            listaColores.add(Color.GRAY);      
            listaColores.add(Color.WHITE);      
            listaColores.add(Color.ORANGE);     
            listaColores.add(Color.PINK);      
            
           
            for (int i = 0; i < listaColores.size(); i++) {
                tablaColores.put(listaColores.get(i), i + 1);
            }
            
         
            StyledDocument doc = textPane.getStyledDocument();
            String contenido = "";
            try {
                contenido = doc.getText(0, doc.getLength());
            } catch (BadLocationException e) {
                throw new IOException("Error al leer el contenido del documento", e);
            }
         
            for (int i = 0; i < contenido.length(); i++) {
                Element element = doc.getCharacterElement(i);
                AttributeSet attrs = element.getAttributes();
                Color color = StyleConstants.getForeground(attrs);
                
                if (color != null && !tablaColores.containsKey(color)) {
                    listaColores.add(color);
                    tablaColores.put(color, listaColores.size());
                }
            }
            
       
            writer.write("{\\rtf1\\ansi\\deff0");
            
            
            writer.write("{\\fonttbl");
            writer.write("{\\f0\\fnil Times New Roman;}");
            writer.write("{\\f1\\fnil Arial;}");
            writer.write("{\\f2\\fnil Courier New;}");
            writer.write("}");
            
           
            writer.write("{\\colortbl;");
            for (Color color : listaColores) {
                writer.write("\\red" + color.getRed() + 
                           "\\green" + color.getGreen() + 
                           "\\blue" + color.getBlue() + ";");
            }
            writer.write("}");
            
            for (int i = 0; i < contenido.length(); i++) {
                char c = contenido.charAt(i);
                Element element = doc.getCharacterElement(i);
                AttributeSet attrs = element.getAttributes();
                
                String fontFamily = StyleConstants.getFontFamily(attrs);
                int fontSize = StyleConstants.getFontSize(attrs);
                boolean bold = StyleConstants.isBold(attrs);
                boolean italic = StyleConstants.isItalic(attrs);
                boolean underline = StyleConstants.isUnderline(attrs);
                Color color = StyleConstants.getForeground(attrs);
                
                writer.write("{");
                
               
                int fontIndex = getFont(fontFamily);
                writer.write("\\f" + fontIndex);
                
              
                writer.write("\\fs" + (fontSize * 2));
                
               
                if (bold) writer.write("\\b");
                if (italic) writer.write("\\i");
                if (underline) writer.write("\\ul");
                
             
                if (color != null) {
                    Integer colorIndex = tablaColores.get(color);
                    if (colorIndex != null) {
                        writer.write("\\cf" + colorIndex);
                    }
                }
                
                writer.write(" ");
                
               
                if (c == '\n') {
                    writer.write("\\par");
                } else if (c == '{' || c == '}' || c == '\\') {
                    writer.write("\\" + c);
                } else if ((int)c > 127) {
                    writer.write("\\u" + (int)c + "?");
                } else {
                    writer.write(c);
                }
                
                writer.write("}");
            }
            
           
            writer.write("}");
        }
    }
    
    private int getFont(String fontFamily) {
        switch (fontFamily.toLowerCase()) {
            case "times new roman":
            case "times":
            case "serif":
                return 0;
            case "arial":
            case "helvetica":
            case "sans-serif":
                return 1;
            case "courier new":
            case "courier":
            case "monospaced":
                return 2;
            default:
                return 1; 
        }
    }
    
   
    
    public DocumentoFormateado cargarRTF(File file) throws IOException {
        StringBuilder contenidoRTF = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenidoRTF.append(linea).append("\n");
            }
        }
        
        String textoPlano = extraerTextoRTF(contenidoRTF.toString());
        
        DocumentoFormateado documento = new DocumentoFormateado();
        documento.setContenido(textoPlano);
        
        return documento;
    }
    
    private String extraerTextoRTF(String rtf) {
        StringBuilder texto = new StringBuilder();
        boolean dentroDeGrupo = false;
        boolean escapando = false;
        
        for (int i = 0; i < rtf.length(); i++) {
            char c = rtf.charAt(i);
            
            if (escapando) {
                if (c == 'p' && i + 2 < rtf.length() && rtf.substring(i, i + 3).equals("par")) {
                    texto.append('\n');
                    i += 2;
                }
                escapando = false;
            } else if (c == '\\') {
                escapando = true;
            } else if (c == '{') {
                dentroDeGrupo = true;
            } else if (c == '}') {
                dentroDeGrupo = false;
            } else if (!dentroDeGrupo && c != '\n' && c != '\r') {
                texto.append(c);
            }
        }
        
        return texto.toString();
    }
    
    public String cargarTexto(File file) throws IOException {
        cont = new StringBuilder();
        String linea;
        try {
            bReader = new BufferedReader(new FileReader(file));
            while((linea = bReader.readLine()) != null) {
                cont.append(linea).append("\n");
            }
        } catch(IOException io) {
            System.out.println("Ocurrió un error en disco. " + io.getMessage());
            throw io;
        } finally {
            if (bReader != null) {
                try { bReader.close(); } catch (IOException e) {}
            }
        }
        
        return cont.toString();
    }
    
    public void exportar(String cont, File file) throws IOException {
        try {
            bWriter = new BufferedWriter(new FileWriter(file));
            bWriter.write(cont);
        } catch(IOException io) {
            System.out.println("Ocurrió un error en disco. " + io.getMessage());
            throw io;
        } finally {
            if (bWriter != null) {
                try { bWriter.close(); } catch (IOException e) {  }
            }
        }
    }
}