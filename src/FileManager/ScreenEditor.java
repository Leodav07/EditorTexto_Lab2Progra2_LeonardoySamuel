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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
    private boolean documentoModificado = false;
    
    public ScreenEditor() {
        this.documentoActual = new DocumentoFormateado();
        this.gestorFormato = new GestorFormato();
        this.gestorArchivo = new GestorArchivos();
        inicializarEditor();
    }
    
    private void inicializarEditor() {
        setTitle("Editor de Texto - Nuevo Documento");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        interfaz = new InterfazUsuario(this);
        interfaz.initComps(); 
        interfaz.crearLayout(); 
        interfaz.configurarEventos(); 
        
        add(interfaz.getPanel(), BorderLayout.CENTER);
        
        configurarEventos();
        interfaz.getTextPane().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { documentoModificado = true; }
            @Override
            public void removeUpdate(DocumentEvent e) { documentoModificado = true; }
            @Override
            public void changedUpdate(DocumentEvent e) { documentoModificado = true; }
        });
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
        if (documentoModificado) {
            int opcion = JOptionPane.showConfirmDialog(this,"¿Desea guardar los cambios antes de continuar?", "Documento modificado", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
            );
            
            switch (opcion) {
                case JOptionPane.YES_OPTION:
                    guardarArchivo();
                    return true;
                case JOptionPane.NO_OPTION:
                    return true;
                case JOptionPane.CANCEL_OPTION:
                default:
                    return false;
            }
        }
        return true;
    }
    
    public void nuevoDocumento() {
        if (confirmarGuardado()) {
            documentoActual = new DocumentoFormateado();
            archivoActual = null;
            interfaz.limpiarTexto();
            documentoModificado = false;
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
                documentoActual = gestorArchivo.cargarDoc(archivoActual);
                
                if (documentoActual != null) {
                    interfaz.setTexto(documentoActual.getContenido());
                    gestorFormato.aplicarFormato(interfaz.getTextPane(), documentoActual.getFormatos());
                    documentoModificado = false;
                    setTitle("Editor de Texto   " + archivoActual.getName());
                } else {
                    JOptionPane.showMessageDialog(this,"No se pudo cargar el archivo", "Error",  JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al abrir el archivo: " + e.getMessage(), "Error",  JOptionPane.ERROR_MESSAGE);
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
            documentoActual.setFormatos((ArrayList<FormatoTexto>) gestorFormato.getFormatos(interfaz.getTextPane()));
            
            gestorArchivo.guardarDoc(documentoActual, archivo);
            archivoActual = archivo;
            documentoModificado = false;
            setTitle("Editor de Texto   " + archivo.getName());
            
            JOptionPane.showMessageDialog(this, "Archivo guardado exitosamente",  "Guardar", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + e.getMessage(),  "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void aplicarFormatoSeleccion() {
        gestorFormato.formatoSeleccion(
            interfaz.getTextPane(),
            interfaz.getFuenteSeleccionada(),
            interfaz.getTamanoSeleccionado(),
            interfaz.getEstiloSeleccionado(),
            interfaz.getColorSeleccionado()
        );
        documentoModificado = true; 
    }
    
    public InterfazUsuario getInterfaz() {
        return interfaz;
    }
    
   
}