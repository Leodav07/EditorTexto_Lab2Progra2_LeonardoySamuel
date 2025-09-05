/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FileManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import javax.swing.*;

/**
 *
 * @author unwir
 */
public class InterfazUsuario {

    private JPanel panelPrincipal;
    private JTextPane textPane;
    private JComboBox<String> fuentes;
    private JComboBox<Integer> tamanos;
    private JToggleButton cursiva;
    private JToggleButton negrita;
    private JToggleButton subrayar;
    private JButton[] botonColor;
    private Color curColor = Color.BLACK;
    private ScreenEditor tE;
    private Color[] colores = {
        Color.WHITE, Color.BLACK, Color.BLUE, Color.RED,
        Color.YELLOW, Color.GRAY, Color.GREEN, Color.MAGENTA,
        Color.ORANGE, Color.PINK, Color.CYAN
    };

    public InterfazUsuario(ScreenEditor tE) {
        this.tE = tE;
    }

    public void initComps() { 
        panelPrincipal = new JPanel(new BorderLayout()); 

        textPane = new JTextPane();
        textPane.setFont(new Font("Arial", Font.PLAIN, 12));

        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = e.getAvailableFontFamilyNames();
        fuentes = new JComboBox<>(fontNames);

        Integer[] size = {8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 42, 48, 56, 64, 72, 96, 144, 190, 240, 300};
        tamanos = new JComboBox<>(size);
        tamanos.setSelectedItem(12); 

        negrita = new JToggleButton("B");
        negrita.setFont(new Font("Arial", Font.BOLD, 12));

        cursiva = new JToggleButton("I");
        cursiva.setFont(new Font("Arial", Font.ITALIC, 12));

        subrayar = new JToggleButton("U");
        subrayar.setFont(new Font("Arial", Font.PLAIN, 12));

        botonColor = new JButton[colores.length];
        for (int i = 0; i < colores.length; i++) {
            botonColor[i] = new JButton();
            botonColor[i].setBackground(colores[i]);
            botonColor[i].setPreferredSize(new Dimension(20, 20));
            final Color color = colores[i];
            botonColor[i].addActionListener(em -> {
                curColor = color;
                tE.aplicarFormatoSeleccion();
            });
        }
    }

    public void crearLayout() {
        JPanel panelHerramientas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnAbrir = new JButton("Abrir");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnGuardarComo = new JButton("Guardar Como");
        
        btnNuevo.addActionListener(e -> tE.nuevoDocumento());
        btnAbrir.addActionListener(e -> tE.abrirArchivo());
        btnGuardar.addActionListener(e -> tE.guardarArchivo());
        btnGuardarComo.addActionListener(e -> tE.guardarComo());
        
        panelHerramientas.add(btnNuevo);
        panelHerramientas.add(btnAbrir);
        panelHerramientas.add(btnGuardar);
        panelHerramientas.add(btnGuardarComo);
        panelHerramientas.add(new JSeparator(SwingConstants.VERTICAL));
        panelHerramientas.add(new JLabel("Fuente:"));
        panelHerramientas.add(fuentes);
        panelHerramientas.add(new JLabel("Tamaño:"));
        panelHerramientas.add(tamanos);
        panelHerramientas.add(negrita);
        panelHerramientas.add(cursiva);
        panelHerramientas.add(subrayar);
        
        JPanel panelColores = new JPanel(new FlowLayout());
        panelColores.add(new JLabel("Colores:"));
        for (JButton boton : botonColor) {
            panelColores.add(boton);
        }
        
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelHerramientas, BorderLayout.NORTH);
        panelSuperior.add(panelColores, BorderLayout.SOUTH);
        
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
    }

    public void configurarEventos() { 
        fuentes.addActionListener(e -> tE.aplicarFormatoSeleccion());
        tamanos.addActionListener(e -> tE.aplicarFormatoSeleccion());
        negrita.addActionListener(e -> tE.aplicarFormatoSeleccion());
        cursiva.addActionListener(e -> tE.aplicarFormatoSeleccion());
        subrayar.addActionListener(e -> tE.aplicarFormatoSeleccion());
    }

    public JPanel getPanel() {
        return panelPrincipal;
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public String getFuenteSeleccionada() {
        return (String) fuentes.getSelectedItem();
    }

    public int getTamanoSeleccionado() {
        return (Integer) tamanos.getSelectedItem();
    }

    public Color getColorSeleccionado() {
        return curColor;
    }

    public int getEstiloSeleccionado() {
        int estilo = Font.PLAIN;
        if (negrita.isSelected()) {
            estilo |= Font.BOLD;
        }
        if (cursiva.isSelected()) {
            estilo |= Font.ITALIC;
        }
        if (subrayar.isSelected()) {
            estilo |= 4;
        }
        return estilo;
    }

    public String getTexto() {
        return textPane.getText();
    }

    public void setTexto(String texto) {
        textPane.setText(texto);
    }

    public void limpiarTexto() {
        textPane.setText("");
        textPane.getStyledDocument().setCharacterAttributes(0, 0, textPane.getStyle("default"), true);
    }
}