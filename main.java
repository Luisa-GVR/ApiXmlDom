import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document parser = builder.parse(new File("src/sales.xml"));

            // Nuevo XML
            Document documento = builder.newDocument();
            Element elementsDocumentoVentas = documento.createElement("sales_doc");
            documento.appendChild(elementsDocumentoVentas);

            System.out.println("Escribe el nombre del departamento");
            Scanner sc = new Scanner(System.in);
            String depBuscar = sc.nextLine();

            System.out.println("Escribe un porcentaje entre 5 y 15% (Sin %) que desees incrementar");
            int valorIncremento = sc.nextInt();
            // verificar que los valores sean adecuados
            while (valorIncremento <= 5 || valorIncremento >= 15) {
                System.out.println("Por favor, escriba un valor válido");
                valorIncremento = sc.nextInt();
            }

            NodeList saleRecords = parser.getElementsByTagName("sale_record");
            boolean encontrado = false;
            for (int i = 0; i < saleRecords.getLength(); i++) {
                Element saleRecord = (Element) saleRecords.item(i);
                Element departamentoElement = (Element) saleRecord.getElementsByTagName("department").item(0);
                String depValor = departamentoElement.getTextContent();

                Element newSaleRecord = documento.createElement("sale_record");

                NodeList childNodes = saleRecord.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    if (childNodes.item(j) instanceof Element) {
                        Element childElement = (Element) childNodes.item(j);
                        Element newChildElement = (Element) documento.importNode(childElement, true);

                        if (childElement.getNodeName().equals("sales") && depValor.equalsIgnoreCase(depBuscar)) {
                            double venta = Double.parseDouble(childElement.getTextContent());
                            double nuevaVenta = venta + ((venta * valorIncremento) / 100);
                            newChildElement.setTextContent(String.valueOf(nuevaVenta));
                        }

                        newSaleRecord.appendChild(newChildElement);
                    }
                }

                elementsDocumentoVentas.appendChild(newSaleRecord);

                encontrado = true;
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); // Puedes ajustar el valor 2 según tu preferencia
            DOMSource src = new DOMSource(documento);
            StreamResult result = new StreamResult(new File("new_sales.xml"));
            t.transform(src, result);

            if (!encontrado) {
                System.out.println("No se ha encontrado el departamento");
            } else{
                System.out.println("Proceso terminado");

            }

        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
