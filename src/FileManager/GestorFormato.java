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
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author hnleo
 */
public class GestorFormato {
    StyledDocument doc;
    Style st;
    
    public void formatoSeleccion(JTextPane pane, String fuente, int tamanio, int estilo, Color color){
        int in = pane.getSelectionStart();
        int fin = pane.getSelectionEnd();
        
        if(in == fin)return;
        doc = pane.getStyledDocument();
        st = doc.addStyle("TempStyle", null);
        StyleConstants.setFontFamily(st, fuente);
        StyleConstants.setFontSize(st, tamanio);
        StyleConstants.setBold(st, (estilo & Font.BOLD)!=0);
        StyleConstants.setItalic(st, (estilo & Font.ITALIC)!=0);
        StyleConstants.setUnderline(st, (estilo & 4)!=0);
        StyleConstants.setForeground(st, color);
        doc.setCharacterAttributes(in, fin-in, st, false);
    }
    
    public List<FormatoTexto> getFormatos(JTextPane pane){
        List<FormatoTexto> formats = new ArrayList<>();
        doc = pane.getStyledDocument();
        return formats;
    }
    
    public void aplicarFormato(JTextPane pane, List<FormatoTexto> formatos){
        doc = pane.getStyledDocument();
        for (FormatoTexto f : formatos) {
            st = doc.addStyle("AppliedStyle", null);
             StyleConstants.setFontFamily(st, f.getFontFamily());
             StyleConstants.setFontSize(st, f.getFontSize());
             StyleConstants.setBold(st, (f.getFontStyle() & Font.BOLD) != 0);
            StyleConstants.setItalic(st, (f.getFontStyle() & Font.ITALIC) != 0); 
            StyleConstants.setForeground(st, f.getColor());
            doc.setCharacterAttributes(f.getInicio(), f.getFin() - f.getInicio(), st, false);
        }
    }
}
