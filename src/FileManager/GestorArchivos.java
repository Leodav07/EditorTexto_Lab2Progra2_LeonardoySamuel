/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.util.List;
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
                try { inputDoc.close(); } catch (IOException e) { }
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
            writer.write("{\\rtf1\\ansi\\deff0");
            
            writer.write("{\\fonttbl");
            writer.write("{\\f0\\fnil Times New Roman;}");
            writer.write("{\\f1\\fnil Arial;}");
            writer.write("{\\f2\\fnil Courier New;}");
            writer.write("}");
            
            writer.write("{\\colortbl;");
            writer.write("\\red0\\green0\\blue0;");  
            writer.write("\\red255\\green0\\blue0;");   
            writer.write("\\red0\\green255\\blue0;");  
            writer.write("\\red0\\green0\\blue255;");    
            writer.write("\\red255\\green255\\blue0;");  
            writer.write("\\red255\\green0\\blue255;");  
            writer.write("\\red0\\green255\\blue255;"); 
            writer.write("\\red128\\green128\\blue128;"); 
            writer.write("\\red255\\green255\\blue255;"); 
            writer.write("\\red255\\green165\\blue0;");  
            writer.write("\\red255\\green192\\blue203;"); 
            writer.write("}");
            
            StyledDocument doc = textPane.getStyledDocument();
            String contenido = "";
            try {
                contenido = doc.getText(0, doc.getLength());
            } catch (BadLocationException e) {
                throw new IOException("Error al leer el contenido del documento", e);
            }
            
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
                
                // Fuente
                int fontIndex = getFont(fontFamily);
                writer.write("\\f" + fontIndex);
                
                writer.write("\\fs" + (fontSize * 2));
                
                // Estilo
                if (bold) writer.write("\\b");
                if (italic) writer.write("\\i");
                if (underline) writer.write("\\ul");
                
                // Color
                int colorIndex = getColorIndex(color);
                if (colorIndex > 0) {
                    writer.write("\\cf" + colorIndex);
                }
                
                writer.write(" ");
                
                // Escribir el carácter
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
            
            // Cerrar RTF
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
    
    private int getColorIndex(Color color) {
        if (color == null) return 1; 
        
        if (color.equals(Color.BLACK)) return 1;
        if (color.equals(Color.RED)) return 2;
        if (color.equals(Color.GREEN)) return 3;
        if (color.equals(Color.BLUE)) return 4;
        if (color.equals(Color.YELLOW)) return 5;
        if (color.equals(Color.MAGENTA)) return 6;
        if (color.equals(Color.CYAN)) return 7;
        if (color.equals(Color.GRAY)) return 8;
        if (color.equals(Color.WHITE)) return 9;
        if (color.equals(Color.ORANGE)) return 10;
        if (color.equals(Color.PINK)) return 11;
        
        return 1;
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
                try { bWriter.close(); } catch (IOException e) { /* ignore */ }
            }
        }
    }
}