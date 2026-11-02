/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subsystem2;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
                case "CREATE_CATEGORY": {
                    String name = getTagValue(root, "name");
                    String parentIdStr = getTagValue(root, "parentId");
                    Integer parentId = parentIdStr != null && !parentIdStr.isEmpty()
                            ? Integer.parseInt(parentIdStr) : null;
                    return JPAExecutor.createCategory(name, parentId);
                }

                case "CREATE_PRODUCT": {
                    String name = getTagValue(root, "name");
                    String description = getTagValue(root, "description");
                    double price = Double.parseDouble(getTagValue(root, "price"));
                    int discount = Integer.parseInt(getTagValue(root, "discount"));
                    int sellerId = Integer.parseInt(getTagValue(root, "sellerId"));
                    int categoryId = Integer.parseInt(getTagValue(root, "categoryId"));
                    return JPAExecutor.createProduct(name, description, price, discount, sellerId, categoryId);
                }

                case "UPDATE_PRODUCT_PRICE": {
                    int productId = Integer.parseInt(getTagValue(root, "productId"));
                    double price = Double.parseDouble(getTagValue(root, "price"));
                    int sellerId = Integer.parseInt(getTagValue(root, "sellerId"));
                    return JPAExecutor.updateProductPrice(productId, price, sellerId);
                }

                case "SET_DISCOUNT": {
                    int productId = Integer.parseInt(getTagValue(root, "productId"));
                    int discount = Integer.parseInt(getTagValue(root, "discount"));
                    int sellerId = Integer.parseInt(getTagValue(root, "sellerId"));
                    return JPAExecutor.setDiscount(productId, discount, sellerId);
                }

                case "ADD_TO_CART": {
                    int userId = Integer.parseInt(getTagValue(root, "userId"));
                    int productId = Integer.parseInt(getTagValue(root, "productId"));
                    int quantity = Integer.parseInt(getTagValue(root, "quantity"));
                    return JPAExecutor.addToCart(userId, productId, quantity);
                }

                case "REMOVE_FROM_CART": {
                    int userId = Integer.parseInt(getTagValue(root, "userId"));
                    int productId = Integer.parseInt(getTagValue(root, "productId"));
                    int quantity = Integer.parseInt(getTagValue(root, "quantity"));
                    return JPAExecutor.removeFromCart(userId, productId, quantity);
                }
                
                case "CLEAR_CART": {
                    int userId = Integer.parseInt(getTagValue(root, "userId"));
                    return JPAExecutor.clearCart(userId);
                }

                case "ADD_TO_WISHLIST": {
                    int userId = Integer.parseInt(getTagValue(root, "userId"));
                    int productId = Integer.parseInt(getTagValue(root, "productId"));
                    return JPAExecutor.addToWishlist(userId, productId);
                }

                case "REMOVE_FROM_WISHLIST": {
                    int userId = Integer.parseInt(getTagValue(root, "userId"));
                    int productId = Integer.parseInt(getTagValue(root, "productId"));
                    return JPAExecutor.removeFromWishlist(userId, productId);
                }

                case "GET_ALL_CATEGORIES":
                    return JPAExecutor.getAllCategories();

                case "GET_SELLER_PRODUCTS": {
                    int sellerId = Integer.parseInt(getTagValue(root, "userId"));
                    return JPAExecutor.getProductsBySeller(sellerId);
                }

                case "GET_CART": {
                    int userId = Integer.parseInt(getTagValue(root, "userId"));
                    return JPAExecutor.getCart(userId);
                }

                case "GET_WISHLIST": {
                    int userId = Integer.parseInt(getTagValue(root, "userId"));
                    return JPAExecutor.getWishlist(userId);
                }

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

