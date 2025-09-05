/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

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
        configurarMenus();

        interfaz.getTextPane().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                documentoModificado = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentoModificado = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentoModificado = true;
            }
        });
    }

    private void configurarMenus() {
        JMenuBar menuBar = new JMenuBar();

        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem itemNuevo = new JMenuItem("Nuevo");
        itemNuevo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        itemNuevo.addActionListener(e -> nuevoDocumento());

        JMenuItem itemAbrir = new JMenuItem("Abrir...");
        itemAbrir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        itemAbrir.addActionListener(e -> abrirArchivo());

        JMenuItem itemGuardar = new JMenuItem("Guardar");
        itemGuardar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        itemGuardar.addActionListener(e -> guardarArchivo());

        JMenuItem itemGuardarComo = new JMenuItem("Guardar como...");
        itemGuardarComo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        itemGuardarComo.addActionListener(e -> guardarComo());

        JSeparator separador1 = new JSeparator();

        // Cambiar el orden - RTF como formato principal
        JMenuItem itemAbrirJava = new JMenuItem("Abrir formato Java...");
        itemAbrirJava.addActionListener(e -> abrirArchivoJava());

        JMenuItem itemExportarJava = new JMenuItem("Exportar a formato Java...");
        itemExportarJava.addActionListener(e -> exportarFormatoJava());

        JSeparator separador2 = new JSeparator();

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        itemSalir.addActionListener(e -> {
            if (confirmarGuardado()) {
                System.exit(0);
            }
        });

        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemGuardarComo);
        menuArchivo.add(separador1);
        menuArchivo.add(itemAbrirJava);
        menuArchivo.add(itemExportarJava);
        menuArchivo.add(separador2);
        menuArchivo.add(itemSalir);

        JMenu menuFormato = new JMenu("Formato");
        JMenuItem itemAplicarFormato = new JMenuItem("Aplicar formato a selección");
        itemAplicarFormato.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        itemAplicarFormato.addActionListener(e -> aplicarFormatoSeleccion());
        menuFormato.add(itemAplicarFormato);

        menuBar.add(menuArchivo);
        menuBar.add(menuFormato);
        setJMenuBar(menuBar);
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
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea guardar los cambios antes de continuar?",
                    "Documento modificado",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            switch (opcion) {
                case JOptionPane.YES_OPTION:
                    guardarArchivo();
                    return !documentoModificado; 
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
        if (!confirmarGuardado()) {
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Rich Text Format (.rtf)", "rtf"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File archivo = fileChooser.getSelectedFile();
                documentoActual = gestorArchivo.cargarRTF(archivo);

                if (documentoActual != null) {
                    interfaz.setTexto(documentoActual.getContenido());
                    archivoActual = archivo;
                    documentoModificado = false;
                    setTitle("Editor de Texto - " + archivo.getName());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se pudo cargar el archivo RTF",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al abrir el archivo RTF: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void guardarArchivo() {
        if (archivoActual == null) {
            guardarComo();
        } else {
            guardarRTF(archivoActual);
        }
    }

    public void abrirArchivoJava() {
        if (!confirmarGuardado()) {
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Documentos Editor Java (.jdoc)", "jdoc"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File archivo = fileChooser.getSelectedFile();
                documentoActual = gestorArchivo.cargarDoc(archivo);

                if (documentoActual != null) {
                    interfaz.setTexto(documentoActual.getContenido());
                    gestorFormato.aplicarFormato(interfaz.getTextPane(), documentoActual.getFormatos());
                    documentoModificado = false;
                    setTitle("Editor de Texto - " + archivo.getName() + " (formato Java)");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al abrir el archivo: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void exportarFormatoJava() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Documentos Editor Java (.jdoc)", "jdoc"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            if (!archivo.getName().endsWith(".jdoc")) {
                archivo = new File(archivo.getPath() + ".jdoc");
            }

            try {
                documentoActual.setContenido(interfaz.getTexto());
                documentoActual.setFormatos((ArrayList<FormatoTexto>) gestorFormato.getFormatos(interfaz.getTextPane()));
                gestorArchivo.guardarDoc(documentoActual, archivo);

                JOptionPane.showMessageDialog(this,
                        "Archivo exportado exitosamente al formato Java.\n"
                      ,
                        "Exportación exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error al exportar: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void guardarComo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Rich Text Format (.rtf)", "rtf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            if (!archivo.getName().endsWith(".rtf")) {
                archivo = new File(archivo.getPath() + ".rtf");
            }
            guardarRTF(archivo);
        }
    }

    private void guardarRTF(File archivo) {
        try {
            gestorArchivo.exportarRTF(interfaz.getTextPane(), archivo);
            archivoActual = archivo;
            documentoModificado = false;
            setTitle("Editor de Texto - " + archivo.getName());

            JOptionPane.showMessageDialog(this,
                    "Archivo guardado exitosamente.\n"
                  ,
                    "Guardar",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el archivo: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void exportarParaWord() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Rich Text Format (.rtf)", "rtf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            if (!archivo.getName().endsWith(".rtf")) {
                archivo = new File(archivo.getPath() + ".rtf");
            }

            try {
                gestorArchivo.exportarRTF(interfaz.getTextPane(), archivo);

                JOptionPane.showMessageDialog(this,
                        "Archivo exportado exitosamente para Word.\n"
                        ,
                        "Exportación exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error al exportar el archivo: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardar(File archivo) {
        try {
            documentoActual.setContenido(interfaz.getTexto());
            documentoActual.setFormatos((ArrayList<FormatoTexto>) gestorFormato.getFormatos(interfaz.getTextPane()));

            gestorArchivo.guardarDoc(documentoActual, archivo);
            archivoActual = archivo;
            documentoModificado = false;
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
