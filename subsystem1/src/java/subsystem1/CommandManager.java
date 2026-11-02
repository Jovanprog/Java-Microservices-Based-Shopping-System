/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subsystem1;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author Jovan
 */
public class CommandManager {
    public static String doCommand(String operation, String xmlPayload) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("root");
            if(xmlPayload != null) {
                doc = builder.parse(new InputSource(new StringReader(xmlPayload)));
                root = doc.getDocumentElement();
            } 

            switch (operation) {
                case "LOGIN":
                    String username = getTagValue(root, "username");
                    String password = getTagValue(root, "password");
                    return JPAExecutor.login(username, password);

                case "CREATE_CITY":
                    String cityName = getTagValue(root, "name");
                    System.out.println("CITY NAME: " + cityName);
                    return JPAExecutor.createCity(cityName);

                case "CREATE_USER":
                    String userUsername = getTagValue(root, "username");
                    String userPassword = getTagValue(root, "password");
                    String firstName = getTagValue(root, "firstName");
                    String lastName = getTagValue(root, "lastName");
                    String role = getTagValue(root, "role");
                    String address = getTagValue(root, "address");
                    String cityIdStr = getTagValue(root, "cityId");
                    int cityId = Integer.parseInt(cityIdStr);
                    String balanceStr = getTagValue(root, "balance");
                    double balance = Double.parseDouble(balanceStr);
                    return JPAExecutor.createUser(userUsername, userPassword, firstName, lastName, role, balance, address, cityId);

                case "INCREASE_BALANCE":{
                    int idUser = Integer.parseInt(getTagValue(root, "idUser"));
                    String amountStr = getTagValue(root, "amount");
                    double amount = Double.parseDouble(amountStr);
                    return JPAExecutor.increaseBalance(idUser, amount);
                }
                case "DECREASE_BALANCE":{
                    int userId = Integer.parseInt(getTagValue(root, "userId"));
                    String amountStr = getTagValue(root, "amount");
                    double amount = Double.parseDouble(amountStr);
                    return JPAExecutor.decreaseBalance(userId, amount);
                }
                case "GET_BALANCE":
                    int userId = Integer.parseInt(getTagValue(root, "userId"));
                    return JPAExecutor.getBalance(userId);

                case "UPDATE_USER_CITY_AND_ADDRESS":
                    int idUser = Integer.parseInt(getTagValue(root, "idUser"));
                    String updAddress = getTagValue(root, "address");
                    String updCityIdStr = getTagValue(root, "cityId");
                    int updCityId = Integer.parseInt(updCityIdStr);
                    return JPAExecutor.updateAddressAndCity(idUser, updAddress, updCityId);

                case "GET_ALL_CITIES":
                    return JPAExecutor.getAllCities();

                case "GET_ALL_USERS":
                    return JPAExecutor.getAllUsers();
                    
                case "GET_CITY": {
                    int cityIdGet = Integer.parseInt(getTagValue(root, "cityId"));
                    return JPAExecutor.cityExists(cityIdGet);
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
        NodeList  nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }
}