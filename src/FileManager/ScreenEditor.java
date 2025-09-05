/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;


import javax.swing.*;
import javax.swing.text.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hnleo
 */

public class ScreenEditor extends JFrame {
    private InterfazUsuario interfaz;
    private DocumentoFormateado documentoActual;
    private File archivoActual;
    private GestorFormato gestorFormato;
    private GestorArchivos gestorArchivo;
    
    public ScreenEditor() {
        this.documentoActual = new DocumentoFormateado();
        this.gestorFormato = new GestorFormato();
        this.gestorArchivo = new GestorArchivos();
        inicializarEditor();
    }
    
    private void inicializarEditor() {
        setTitle("Editor de Texto - Nuevo Documento");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        interfaz = new InterfazUsuario(this);
        add(interfaz.getPanel(), BorderLayout.CENTER);
        
        configurarEventos();
    }
    
    private void configurarEventos() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirmarGuardado()) {
                    System.exit(0);
                }
            }
        });
    }
      private boolean confirmarGuardado() {
        return true;
    }
    public void nuevoDocumento() {
        if (confirmarGuardado()) {
            documentoActual = new DocumentoFormateado();
            archivoActual = null;
            interfaz.limpiarTexto();
            setTitle("Editor de Texto - Nuevo Documento");
        }
    }
    
    public void abrirArchivo() {
        if (!confirmarGuardado()) return;
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Documentos de texto (.docx)", "docx"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                archivoActual = fileChooser.getSelectedFile();
                documentoActual = GestorArchivos.cargarDocumento(archivoActual);
                
                interfaz.setTexto(documentoActual.getContenido());
                gestorFormato.aplicarFormatos(interfaz.getTextPane(), documentoActual.getFormatos());
                
                setTitle("Editor de Texto - " + archivoActual.getName());
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al abrir el archivo: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void guardarArchivo() {
        if (archivoActual == null) {
            guardarComo();
        } else {
            guardar(archivoActual);
        }
    }
    
    public void guardarComo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Documentos de texto (.docx)", "docx"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            if (!archivo.getName().endsWith(".docx")) {
                archivo = new File(archivo.getPath() + ".docx");
            }
            guardar(archivo);
        }
    }
    
    private void guardar(File archivo) {
        try {
            documentoActual.setContenido(interfaz.getTexto());
            documentoActual.setFormatos(gestorFormato.extraerFormatos(interfaz.getTextPane()));
            
            gestorArchivo.guardarDoc(documentoActual, archivo);
            archivoActual = archivo;
            setTitle("Editor de Texto - " + archivo.getName());
            
            JOptionPane.showMessageDialog(this, 
                "Archivo guardado exitosamente", 
                "Guardar", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar el archivo: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
 
    public void aplicarFormatoSeleccion() {
        gestorFormato.aplicarFormatoASeleccion(
            interfaz.getTextPane(),
            interfaz.getFuenteSeleccionada(),
            interfaz.getTamanoSeleccionado(),
            interfaz.getEstiloSeleccionado(),
            interfaz.getColorSeleccionado()
        );
    }
    
    public InterfazUsuario getInterfaz() {
        return interfaz;
   }
}
