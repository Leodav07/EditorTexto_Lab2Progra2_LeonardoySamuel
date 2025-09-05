/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author hnleo
 */
public class GestorFormato {
    private StyledDocument doc;
    private Style st;
    
    public void formatoSeleccion(JTextPane pane, String fuente, int tamanio, int estilo, Color color){
        int inicio = pane.getSelectionStart();
        int fin = pane.getSelectionEnd();
        
        if(inicio == fin) return;
        
        doc = pane.getStyledDocument();
        st = doc.addStyle("TempStyle", null);
        
        StyleConstants.setFontFamily(st, fuente);
        StyleConstants.setFontSize(st, tamanio);
        StyleConstants.setBold(st, (estilo & Font.BOLD) != 0);
        StyleConstants.setItalic(st, (estilo & Font.ITALIC) != 0);
        StyleConstants.setUnderline(st, (estilo & 4) != 0);
        StyleConstants.setForeground(st, color);
        
        doc.setCharacterAttributes(inicio, fin - inicio, st, false);
    }
    
    public List<FormatoTexto> getFormatos(JTextPane pane){
        List<FormatoTexto> formatos = new ArrayList<>();
        doc = pane.getStyledDocument();
        
        try {
            for (int i = 0; i < doc.getLength(); i++) {
                Element element = doc.getCharacterElement(i);
                AttributeSet attrs = element.getAttributes();
                
                String fontFamily = StyleConstants.getFontFamily(attrs);
                int fontSize = StyleConstants.getFontSize(attrs);
                boolean bold = StyleConstants.isBold(attrs);
                boolean italic = StyleConstants.isItalic(attrs);
                boolean underline = StyleConstants.isUnderline(attrs);
                Color color = StyleConstants.getForeground(attrs);
                
                int fontStyle = Font.PLAIN;
                if (bold) fontStyle |= Font.BOLD;
                if (italic) fontStyle |= Font.ITALIC;
                if (underline) fontStyle |= 4;
                
                int startOffset = element.getStartOffset();
                int endOffset = element.getEndOffset();
                
                if (!fontFamily.equals("Dialog") || fontSize != 12 || fontStyle != Font.PLAIN || !color.equals(Color.BLACK)) {
                    FormatoTexto formato = new FormatoTexto(
                        fontFamily, fontSize, fontStyle, color, startOffset, endOffset
                    );
                    formatos.add(formato);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al extraer formatos: " + e.getMessage());
        }
        
        return formatos;
    }
    
    public void aplicarFormato(JTextPane pane, ArrayList<FormatoTexto> formatos){
        if (formatos == null) return;
        
        doc = pane.getStyledDocument();
        
        for (FormatoTexto f : formatos) {
            try {
                st = doc.addStyle("AppliedStyle", null);
                StyleConstants.setFontFamily(st, f.getFontFamily());
                StyleConstants.setFontSize(st, f.getFontSize());
                StyleConstants.setBold(st, (f.getFontStyle() & Font.BOLD) != 0);
                StyleConstants.setItalic(st, (f.getFontStyle() & Font.ITALIC) != 0);
                StyleConstants.setUnderline(st, (f.getFontStyle() & 4) != 0);
                StyleConstants.setForeground(st, f.getColor());
                
                int length = Math.min(f.getFin() - f.getInicio(), doc.getLength() - f.getInicio());
                if (length > 0 && f.getInicio() < doc.getLength()) {
                    doc.setCharacterAttributes(f.getInicio(), length, st, false);
                }
            } catch (Exception e) {
                System.err.println("Error al aplicar formato: " + e.getMessage());
            }
        }
    }
    
    public void aplicarFormatoASeleccion(JTextPane pane, String fuente, int tamanio, int estilo, Color color) {
        formatoSeleccion(pane, fuente, tamanio, estilo, color);
    }
}