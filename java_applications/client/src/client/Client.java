package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

/**
 *
 * @author Jovan
 */
public class Client {

    private static final String BASE_URL = "http://localhost:8080/centralServer/api";

    private static Integer loggedInUserId = null;
    private static String loggedInUsername = null;
    private static String loggedInRole = null;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        while(true){
            if(loggedInUserId == null){
                while (loggedInUserId == null) {
                    System.out.println("=== LOGIN REQUIRED ===");
                    System.out.println("1 - Login");
                    System.out.println("0 - Exit");
                    System.out.print("Choice: ");
                    String c = scanner.nextLine().trim();
                    if ("0".equals(c)) return;
                    if ("1".equals(c)) {
                        try {
                            doLogin(scanner);
                        } catch (Exception e) {
                            System.out.println("ERROR: " + e.getMessage());
                        }
                    }
                    System.out.println();
                }
            }
            showMenu();
            String choice = scanner.nextLine().trim();
            //if ("0".equals(choice)) break;

            try {
                handleChoice(scanner, choice);
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private static void requireLogin() {
        if (loggedInUserId == null) throw new IllegalStateException("You must be logged in.");
    }

    private static boolean isAdmin() {
        return "administrator".equalsIgnoreCase(loggedInRole);
    }

    private static void showMenu() {
        System.out.println("=== CLIENT MENU ===");
        System.out.println("Logged in as: " + loggedInUsername + " (" + loggedInRole + ")");
        System.out.println("1  - Login (re-login)");
        System.out.println("2  - Logout");
        if (isAdmin()) {
            System.out.println("3  - Create city");
            System.out.println("4  - Create user");
            System.out.println("5  - Increase balance");
            System.out.println("6  - Update user address/city");
        }
        System.out.println("7  - Create category");
        System.out.println("8  - Create product");
        System.out.println("9  - Update product price");
        System.out.println("10 - Set product discount");
        System.out.println("11 - Add to cart");
        System.out.println("12 - Remove from cart");
        System.out.println("13 - Add to wishlist");
        System.out.println("14 - Remove from wishlist");
        System.out.println("15 - Pay (create order + transaction)");
        if (isAdmin()) {
            System.out.println("16 - Get all cities");
            System.out.println("17 - Get all users");
        }
        System.out.println("18 - Get all categories");
        System.out.println("19 - Get seller products");
        System.out.println("20 - Get cart");
        System.out.println("21 - Get wishlist");
        System.out.println("22 - Get user orders");
        System.out.println("23 - Get all orders");
        System.out.println("24 - Get all transactions");
        //System.out.println("0  - Exit");
        System.out.print("Choice: ");
    }

    private static void handleChoice(Scanner scanner, String choice) throws Exception {
        switch (choice) {
            case "1":  doLogin(scanner); break;
            case "2":
                loggedInUserId = null; loggedInUsername = null; loggedInRole = null;
                System.out.println("Logged out.");
                //main(new String[0]);
                break;
            case "3":  if (!isAdmin()) { System.out.println("Admin only."); break; } doCreateCity(scanner); break;
            case "4":  if (!isAdmin()) { System.out.println("Admin only."); break; } doCreateUser(scanner); break;
            case "5":  if (!isAdmin()) { System.out.println("Admin only."); break; } doIncreaseBalance(scanner); break;
            case "6":  if (!isAdmin()) { System.out.println("Admin only."); break; } doUpdateAddress(scanner); break;
            case "7":  doCreateCategory(scanner); break;
            case "8":  doCreateProduct(scanner); break;
            case "9":  doUpdateProductPrice(scanner); break;
            case "10": doSetDiscount(scanner); break;
            case "11": doAddToCart(scanner); break;
            case "12": doRemoveFromCart(scanner); break;
            case "13": doAddToWishlist(scanner); break;
            case "14": doRemoveFromWishlist(scanner); break;
            case "15": doPay(scanner); break;
            case "16":
                if (!isAdmin()) { System.out.println("Admin only."); break; }
                TableFormatter.printCities(get("/cities/all"));
                break;
            case "17":
                if (!isAdmin()) { System.out.println("Admin only."); break; }
                TableFormatter.printUsers(get("/users/all"));
                break;
            case "18": TableFormatter.printCategories(get("/categories/all")); break;
            case "19": doGetSellerProducts(scanner); break;
            case "20": doGetCart(scanner); break;
            case "21": doGetWishlist(scanner); break;
            case "22": doGetUserOrders(scanner); break;
            case "23": TableFormatter.printOrders(get("/orders/all")); break;
            case "24": TableFormatter.printTransactions(get("/transactions/all")); break;
            default:   System.out.println("Unknown option.");
        }
    }

    // ---- Operations ----

    private static void doLogin(Scanner scanner) throws Exception {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        String xml = "<loginRequest><username>" + username + "</username><password>" + password + "</password></loginRequest>";
        String resp = postXml("/users/login", xml);

        int id = extractIntTag(resp, "idUser");
        String role = extractStringTag(resp, "role");
        if (id > 0) {
            loggedInUserId = id;
            loggedInUsername = username;
            loggedInRole = role != null ? role : "user";
            System.out.println("Logged in successfully as " + loggedInUsername + " [" + loggedInRole + "] (ID: " + id + ")");
        } else {
            TableFormatter.printSimpleResponse(resp);
        }
    }

    private static void doCreateCity(Scanner scanner) throws Exception {
        System.out.print("City name: ");
        String name = scanner.nextLine().trim();
        String xml = "<createCityRequest><name>" + name + "</name></createCityRequest>";
        //System.out.println(xml);
        TableFormatter.printCreateCityResponse(postXml("/cities/create", xml));
    }

    private static void doCreateUser(Scanner scanner) throws Exception {
        System.out.print("Username: ");   String username = scanner.nextLine().trim();
        System.out.print("Password: ");   String password = scanner.nextLine().trim();
        System.out.print("First name: "); String first = scanner.nextLine().trim();
        System.out.print("Last name: ");  String last = scanner.nextLine().trim();
        System.out.print("Role (administrator, normal_user): ");  String role = scanner.nextLine().trim();
        System.out.print("Address: ");    String addr     = scanner.nextLine().trim();
        System.out.print("City ID: ");    String cityId   = scanner.nextLine().trim();
        System.out.print("Balance: ");    String balance     = scanner.nextLine().trim();
        
        String xml = "<createUserRequest>" +
                "<username>" + username + "</username>" +
                "<password>" + password + "</password>" +
                "<firstName>" + first + "</firstName>" +
                "<lastName>" + last + "</lastName>" +
                "<role>" + role + "</role>" +
                "<address>" + addr + "</address>" +
                "<balance>" + balance + "</balance>" +
                "<cityId>" + cityId + "</cityId>" +
                "</createUserRequest>";
        TableFormatter.printCreateUserResponse(postXml("/users/create", xml));
    }

    private static void doIncreaseBalance(Scanner scanner) throws Exception {
        requireLogin();
        System.out.print("User ID: ");
        String idUser = scanner.nextLine().trim();
        System.out.print("Amount to add: ");
        String amount = scanner.nextLine().trim();
        String xml = "<increaseBalanceRequest>" +
                "<idUser>" + idUser + "</idUser>" +
                "<amount>" + amount + "</amount>" +
                "</increaseBalanceRequest>";
        TableFormatter.printIncreaseBalanceResponse(putXml("/users/increaseBalance", xml));
    }

    private static void doUpdateAddress(Scanner scanner) throws Exception {
        requireLogin();
        System.out.print("User ID: "); String idUser   = scanner.nextLine().trim();
        System.out.print("New address: "); String addr   = scanner.nextLine().trim();
        System.out.print("New city ID: "); String cityId = scanner.nextLine().trim();
        String xml = "<updateUserRequest>" +
                "<username>" + loggedInUsername + "</username>" +
                "<address>" + addr + "</address>" +
                "<idUser>" + idUser + "</idUser>" +
                "<cityId>" + cityId + "</cityId>" +
                "</updateUserRequest>";
        TableFormatter.printUpdateAddressAndCityResponse(putXml("/users/updateAddress", xml));
    }

    private static void doCreateCategory(Scanner scanner) throws Exception {
        System.out.print("Category name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Parent category id (empty if none): ");
        String parent = scanner.nextLine().trim();

        StringBuilder xml = new StringBuilder("<createCategoryRequest>");
        xml.append("<name>").append(name).append("</name>");
        if (!parent.isEmpty()) xml.append("<parentId>").append(parent).append("</parentId>");
        xml.append("</createCategoryRequest>");
        TableFormatter.printCreateCategoryResponse(postXml("/categories/create", xml.toString()));
    }

    private static void doCreateProduct(Scanner scanner) throws Exception {
        requireLogin();
        System.out.print("Product name: "); String name  = scanner.nextLine().trim();
        System.out.print("Description: ");  String desc  = scanner.nextLine().trim();
        System.out.print("Price: ");        String price = scanner.nextLine().trim();
        System.out.print("Discount: ");        String discount = scanner.nextLine().trim();
        System.out.print("Category ID: ");  String catId = scanner.nextLine().trim();

        String xml = "<createProductRequest>" +
                "<name>" + name + "</name>" +
                "<description>" + desc + "</description>" +
                "<price>" + price + "</price>" +
                "<discount>" + discount + "</discount>" +
                "<sellerId>" + loggedInUserId + "</sellerId>" +
                "<categoryId>" + catId + "</categoryId>" +
                "</createProductRequest>";
        TableFormatter.printCreateProductResponse(postXml("/products/create", xml));
    }

    private static void doUpdateProductPrice(Scanner scanner) throws Exception {
        System.out.print("Product ID: "); String pid   = scanner.nextLine().trim();
        System.out.print("New price: ");  String price = scanner.nextLine().trim();
        String xml = "<updateProductPriceRequest>" +
                "<productId>" + pid + "</productId>" +
                "<price>" + price + "</price>" +
                "<sellerId>" + loggedInUserId + "</sellerId>" +
                "</updateProductPriceRequest>";
        TableFormatter.printUpdateProductPriceResponse(putXml("/products/price", xml));
    }

    private static void doSetDiscount(Scanner scanner) throws Exception {
        System.out.print("Product ID: ");        String pid  = scanner.nextLine().trim();
        System.out.print("Discount (integer %): "); String disc = scanner.nextLine().trim();
        String xml = "<setDiscountRequest>" +
                "<productId>" + pid + "</productId>" +
                "<discount>" + disc + "</discount>" +
                "<sellerId>" + loggedInUserId + "</sellerId>" +
                "</setDiscountRequest>";
        TableFormatter.printSetDiscountResponse(putXml("/products/discount", xml));
    }

    private static void doAddToCart(Scanner scanner) throws Exception {
        requireLogin();
        System.out.print("Product ID: "); String pid = scanner.nextLine().trim();
        System.out.print("Quantity: ");   String qty = scanner.nextLine().trim();
        String xml = "<cartRequest>" +
                "<userId>" + loggedInUserId + "</userId>" +
                "<productId>" + pid + "</productId>" +
                "<quantity>" + qty + "</quantity>" +
                "</cartRequest>";
        TableFormatter.printAddToCartResponse(postXml("/cart/add", xml));
    }

    private static void doRemoveFromCart(Scanner scanner) throws Exception {
        requireLogin();
        System.out.print("Product ID: "); String pid = scanner.nextLine().trim();
        System.out.print("Quantity: ");   String qty = scanner.nextLine().trim();
        String xml = "<cartRequest>" +
                "<userId>" + loggedInUserId + "</userId>" +
                "<productId>" + pid + "</productId>" +
                "<quantity>" + qty + "</quantity>" +
                "</cartRequest>";
        TableFormatter.printRemoveFromCartResponse(putXml("/cart/remove", xml));
    }

    private static void doAddToWishlist(Scanner scanner) throws Exception {
        requireLogin();
        System.out.print("Product ID: "); String pid = scanner.nextLine().trim();
        String xml = "<wishlistRequest>" +
                "<userId>" + loggedInUserId + "</userId>" +
                "<productId>" + pid + "</productId>" +
                "</wishlistRequest>";
        TableFormatter.printAddToWishlistResponse(postXml("/wishlist/add", xml));
    }

    private static void doRemoveFromWishlist(Scanner scanner) throws Exception {
        requireLogin();
        System.out.print("Product ID: "); String pid = scanner.nextLine().trim();
        String xml = "<wishlistRequest>" +
                "<userId>" + loggedInUserId + "</userId>" +
                "<productId>" + pid + "</productId>" +
                "</wishlistRequest>";
        TableFormatter.printRemoveFromWishlistResponse(putXml("/wishlist/remove", xml));
    }

    /*private static void doPay(Scanner scanner) throws Exception {
        requireLogin();
        System.out.print("Delivery address: "); String addr   = scanner.nextLine().trim();
        System.out.print("City ID: ");           String cityId = scanner.nextLine().trim();
        System.out.print("Number of items: ");   int n = Integer.parseInt(scanner.nextLine().trim());

        StringBuilder xml = new StringBuilder("<payRequest>");
        xml.append("<userId>").append(loggedInUserId).append("</userId>");
        xml.append("<address>").append(addr).append("</address>");
        xml.append("<cityId>").append(cityId).append("</cityId>");
        xml.append("<items>");
        for (int i = 0; i < n; i++) {
            System.out.print("Item " + (i+1) + " productId: ");  String pid   = scanner.nextLine().trim();
            System.out.print("Item " + (i+1) + " quantity: ");   String qty   = scanner.nextLine().trim();
            System.out.print("Item " + (i+1) + " unit price: "); String price = scanner.nextLine().trim();
            xml.append("<item>")
               .append("<productId>").append(pid).append("</productId>")
               .append("<quantity>").append(qty).append("</quantity>")
               .append("<unitPrice>").append(price).append("</unitPrice>")
               .append("</item>");
        }
        xml.append("</items></payRequest>");
        TableFormatter.printPayResponse(postXml("/orders/pay", xml.toString()));
    }*/
    private static void doPay(Scanner scanner) throws Exception {
    requireLogin();
    System.out.print("Delivery address: "); String addr   = scanner.nextLine().trim();
    System.out.print("City ID: ");           String cityId = scanner.nextLine().trim();

    String xml = "<payRequest>" +
            "<userId>" + loggedInUserId + "</userId>" +
            "<address>" + addr + "</address>" +
            "<cityId>" + cityId + "</cityId>" +
            "</payRequest>";
    TableFormatter.printPayResponse(postXml("/orders/pay", xml));
}

    private static void doGetSellerProducts(Scanner scanner) throws Exception {
        requireLogin();
        String xml = "<sellerProductsRequest><userId>" + loggedInUserId + "</userId></sellerProductsRequest>";
        TableFormatter.printProducts(postXml("/products/bySeller", xml));
    }

    private static void doGetCart(Scanner scanner) throws Exception {
        requireLogin();
        String xml = "<cartRequest><userId>" + loggedInUserId + "</userId></cartRequest>";
        TableFormatter.printCart(postXml("/cart/get", xml), "CART");
    }

    private static void doGetWishlist(Scanner scanner) throws Exception {
        requireLogin();
        String xml = "<wishlistRequest><userId>" + loggedInUserId + "</userId></wishlistRequest>";
        TableFormatter.printWishlist(postXml("/wishlist/get", xml), "WISHLIST");
    }

    private static void doGetUserOrders(Scanner scanner) throws Exception {
        requireLogin();
        String xml = "<userOrdersRequest><userId>" + loggedInUserId + "</userId></userOrdersRequest>";
        TableFormatter.printOrders(postXml("/orders/user", xml));
    }
    
    //Pomocne funkcije za HTTP
    private static String get(String path) throws Exception {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/xml");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        } finally { conn.disconnect(); }
    }

    private static String postXml(String path, String xml) throws Exception {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) { os.write(xml.getBytes("UTF-8")); }
        int status = conn.getResponseCode();
        java.io.InputStream is = (status >= 400) ? conn.getErrorStream() : conn.getInputStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        } finally { conn.disconnect(); }
    }

    private static String putXml(String path, String xml) throws Exception {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) { os.write(xml.getBytes("UTF-8")); }
        int status = conn.getResponseCode();
        java.io.InputStream is = (status >= 400) ? conn.getErrorStream() : conn.getInputStream();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        } finally { conn.disconnect(); }
    }

    private static int extractIntTag(String xml, String tag) {
        String open = "<" + tag + ">", close = "</" + tag + ">";
        int i1 = xml.indexOf(open), i2 = xml.indexOf(close, i1 + open.length());
        if (i1 == -1 || i2 == -1) return -1;
        try { return Integer.parseInt(xml.substring(i1 + open.length(), i2).trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private static String extractStringTag(String xml, String tag) {
        String open = "<" + tag + ">", close = "</" + tag + ">";
        int i1 = xml.indexOf(open), i2 = xml.indexOf(close, i1 + open.length());
        if (i1 == -1 || i2 == -1) return null;
        return xml.substring(i1 + open.length(), i2).trim();
    }
}