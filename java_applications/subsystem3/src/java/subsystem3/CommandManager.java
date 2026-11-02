/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subsystem3;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import subsystem3.JPAExecutor.PayItem;

public class CommandManager {

    public static String doCommand(String operation, String xmlPayload) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc;
            Element root;
            if (xmlPayload != null && !xmlPayload.trim().isEmpty()) {
                doc = builder.parse(new InputSource(new StringReader(xmlPayload)));
                root = doc.getDocumentElement();
            } else {
                doc = builder.newDocument();
                root = doc.createElement("root");
                doc.appendChild(root);
            }

            switch (operation) {
                case "PAY": {
                    int userId = Integer.parseInt(getTagValue(root, "userId"));
                    String address = getTagValue(root, "address");
                    int cityId = Integer.parseInt(getTagValue(root, "cityId"));

                    List<PayItem> items = new ArrayList<>();
                    NodeList itemNodes = root.getElementsByTagName("item");
                    for (int i = 0; i < itemNodes.getLength(); i++) {
                        Element ie = (Element) itemNodes.item(i);
                        int productId = Integer.parseInt(getTagValue(ie, "productId"));
                        int quantity = Integer.parseInt(getTagValue(ie, "quantity"));
                        BigDecimal unitPrice = new BigDecimal(getTagValue(ie, "unitPrice"));
                        items.add(new PayItem(productId, quantity, unitPrice));
                    }

                    return JPAExecutor.pay(userId, address, cityId, items);
                }

                case "GET_USER_ORDERS": {
                    int userId = Integer.parseInt(getTagValue(root, "userId"));
                    return JPAExecutor.getOrdersByUser(userId);
                }

                case "GET_ALL_ORDERS":
                    return JPAExecutor.getAllOrders();

                case "GET_ALL_TRANSACTIONS":
                    return JPAExecutor.getAllTransactions();

                default:
                    return "<error>UNKNOWN COMMAND " + operation + "</error>";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "<error>" + e.getMessage() + "</error>";
        }
    }

    private static String getTagValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}

