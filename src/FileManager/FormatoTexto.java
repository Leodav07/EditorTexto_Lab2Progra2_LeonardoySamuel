/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;

/**
 *
 * @author unwir
 */
public class FormatoTexto implements Serializable {
    private static final long serialVersionUTD = 1L;
    private String fontFamily;
    private int fontSize;
    private int fontStyle;
    private Color color;
    private int inicio;
    private int fin;

    public FormatoTexto(String fontFamily, int fontSize, int fontStyle, Color color, int inicio, int fin) {
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.fontStyle = fontStyle;
        this.color = color;
        this.inicio = inicio;
        this.fin = fin;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    public int getFin() {
        return fin;
    }

    public void setFin(int fin) {
        this.fin = fin;
    }
    
    public Font crearFont(){
        return new Font(fontFamily, fontStyle, fontSize);
    }
            
            
           
    
    
    
    
}
