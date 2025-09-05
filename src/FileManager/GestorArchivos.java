package FileManager;

import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JTextPane;
import javax.swing.text.*;

/**
 *
 * @author hnleo & unwir
 */
public class GestorArchivos implements Serializable {
    
    // --- MÉTODOS PARA FORMATO NATIVO SERIALIZABLE (.jdoc) ---

    public DocumentoFormateado cargarDoc(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputDoc = new ObjectInputStream(new FileInputStream(file))) {
            return (DocumentoFormateado) inputDoc.readObject();
        } catch(IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar el documento nativo: " + e.getMessage());
            throw e;
        }
    }
    
    public void guardarDoc(DocumentoFormateado doc, File file) throws IOException {
        try (ObjectOutputStream outDoc = new ObjectOutputStream(new FileOutputStream(file))) {
            outDoc.writeObject(doc);
        } catch(IOException io) {
            System.err.println("Ocurrió un error en Disco: " + io.getMessage());
            throw io;
        }
    }
    
    // --- MÉTODOS PARA EXPORTAR/IMPORTAR .DOCX USANDO ZIPENTRY ---

    public void exportarDOCXConZip(JTextPane textPane, File file) throws IOException {
        String contentTypesXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/></Types>";
        String relsXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/></Relationships>";
        
        StyledDocument styledDoc = textPane.getStyledDocument();
        StringBuilder docXmlBuilder = new StringBuilder();
        docXmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        docXmlBuilder.append("<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">");
        docXmlBuilder.append("<w:body><w:p>");

        try {
            for (int i = 0; i < styledDoc.getLength(); i++) {
                Element element = styledDoc.getCharacterElement(i);
                AttributeSet attrs = element.getAttributes();
                String textContent = styledDoc.getText(i, 1);
                
                textContent = textContent.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");

                docXmlBuilder.append("<w:r><w:rPr>");
                if (StyleConstants.isBold(attrs)) docXmlBuilder.append("<w:b/>");
                if (StyleConstants.isItalic(attrs)) docXmlBuilder.append("<w:i/>");
                if (StyleConstants.isUnderline(attrs)) docXmlBuilder.append("<w:u w:val=\"single\"/>");
                
                Color color = StyleConstants.getForeground(attrs);
                if (color != null) {
                    String colorHex = String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
                    docXmlBuilder.append("<w:color w:val=\"").append(colorHex).append("\"/>");
                }
                
                // NUEVO: Guardar el tamaño de la fuente
                // El formato DOCX usa "half-points", por lo que multiplicamos por 2.
                int fontSize = StyleConstants.getFontSize(attrs);
                docXmlBuilder.append("<w:sz w:val=\"").append(fontSize * 2).append("\"/>");
                
                docXmlBuilder.append("</w:rPr><w:t xml:space=\"preserve\">").append(textContent).append("</w:t></w:r>");
            }
        } catch (BadLocationException e) {
            throw new IOException("Error al leer el contenido del editor", e);
        }

        docXmlBuilder.append("</w:p></w:body></w:document>");
        String documentXML = docXmlBuilder.toString();

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
            ZipEntry contentTypesEntry = new ZipEntry("[Content_Types].xml");
            zos.putNextEntry(contentTypesEntry);
            zos.write(contentTypesXML.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
            
            ZipEntry relsEntry = new ZipEntry("_rels/.rels");
            zos.putNextEntry(relsEntry);
            zos.write(relsXML.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
            
            ZipEntry docEntry = new ZipEntry("word/document.xml");
            zos.putNextEntry(docEntry);
            zos.write(documentXML.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
    }

    public DocumentoFormateado cargarDOCXConZip(File file) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("word/document.xml")) {
                    return parsearDocumentXMLSinNodos(zis);
                }
            }
        } catch (Exception e) {
            throw new IOException("Error al leer el archivo DOCX: " + e.getMessage(), e);
        }
        throw new IOException("El archivo no contiene un 'word/document.xml' válido.");
    }

    private DocumentoFormateado parsearDocumentXMLSinNodos(InputStream xmlInputStream) throws IOException {
        StringBuilder xmlContentBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(xmlInputStream, StandardCharsets.UTF_8))) {
            int c;
            while ((c = reader.read()) != -1) {
                xmlContentBuilder.append((char) c);
            }
        }
        String xmlContent = xmlContentBuilder.toString();

        StringBuilder contenido = new StringBuilder();
        ArrayList<FormatoTexto> formatos = new ArrayList<>();

        int currentIndex = 0;
        while (currentIndex < xmlContent.length()) {
            int runStart = xmlContent.indexOf("<w:r>", currentIndex);
            if (runStart == -1) break;
            
            int runEnd = xmlContent.indexOf("</w:r>", runStart);
            if (runEnd == -1) break;

            String runBlock = xmlContent.substring(runStart, runEnd);

            String textoRun = "";
            int textStart = runBlock.indexOf("<w:t");
            if (textStart != -1) {
                textStart = runBlock.indexOf(">", textStart) + 1;
                int textEnd = runBlock.indexOf("</w:t>", textStart);
                if (textEnd != -1) {
                    textoRun = runBlock.substring(textStart, textEnd);
                    textoRun = textoRun.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
                }
            }

            if (textoRun.isEmpty()) {
                currentIndex = runEnd + 6;
                continue;
            }

            int inicio = contenido.length();
            contenido.append(textoRun);
            int fin = contenido.length();

            int fontStyle = Font.PLAIN;
            Color color = Color.BLACK;
            int fontSize = 12; // Tamaño por defecto

            if (runBlock.contains("<w:b/>") || runBlock.contains("<w:b ")) fontStyle |= Font.BOLD;
            if (runBlock.contains("<w:i/>") || runBlock.contains("<w:i ")) fontStyle |= Font.ITALIC;
            if (runBlock.contains("<w:u " )) fontStyle |= 4;

            int colorTagStart = runBlock.indexOf("<w:color w:val=\"");
            if (colorTagStart != -1) {
                int hexStart = colorTagStart + "<w:color w:val=\"".length();
                int hexEnd = runBlock.indexOf("\"", hexStart);
                if (hexEnd != -1) {
                    String hex = runBlock.substring(hexStart, hexEnd);
                    try {
                        color = new Color(Integer.parseInt(hex, 16));
                    } catch (NumberFormatException e) {
                        color = Color.BLACK;
                    }
                }
            }
            
            // NUEVO: Leer el tamaño de la fuente
            int sizeTagStart = runBlock.indexOf("<w:sz w:val=\"");
            if (sizeTagStart != -1) {
                int sizeStart = sizeTagStart + "<w:sz w:val=\"".length();
                int sizeEnd = runBlock.indexOf("\"", sizeStart);
                if (sizeEnd != -1) {
                    String sizeVal = runBlock.substring(sizeStart, sizeEnd);
                    try {
                        // Dividimos entre 2 para convertir de "half-points" a puntos
                        fontSize = Integer.parseInt(sizeVal) / 2;
                    } catch (NumberFormatException e) {
                        fontSize = 12; // Tamaño por defecto si hay error
                    }
                }
            }
            
            // NUEVO: Se actualiza la condición para incluir el tamaño
            if (fontStyle != Font.PLAIN || !color.equals(Color.BLACK) || fontSize != 12) {
                formatos.add(new FormatoTexto("Arial", fontSize, fontStyle, color, inicio, fin));
            }
            
            currentIndex = runEnd + 6;
        }

        return new DocumentoFormateado(contenido.toString(), formatos);
    }
}