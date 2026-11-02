/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jovan
 */
public class TableFormatter {
    static List<String> extractBlocks(String xml, String tag) {
        List<String> result = new ArrayList<>();
        String open = "<" + tag + ">", close = "</" + tag + ">";
        int pos = 0;
        while (true) {
            int start = xml.indexOf(open, pos);
            if (start == -1) break;
            int end = xml.indexOf(close, start + open.length());
            if (end == -1) break;
            result.add(xml.substring(start + open.length(), end));
            pos = end + close.length();
        }
        return result;
    }

    //Izvlaci vrednost jednog taga iz bloka
    static String val(String block, String tag) {
        String open = "<" + tag + ">", close = "</" + tag + ">";
        int i1 = block.indexOf(open);
        if (i1 == -1) return "-";
        int i2 = block.indexOf(close, i1 + open.length());
        if (i2 == -1) return "-";
        String v = block.substring(i1 + open.length(), i2).trim();
        return v.isEmpty() ? "-" : v;
    }
    
    //Funkcije za error-e
    private static boolean hasError(String xml){
        return extractFirst(xml, "error") != null;
    }
    
    private static void printError(String xml){
        String error = extractFirst(xml, "error");
        System.out.println("STATUS: ERROR");
        System.out.println("MESSAGE: " 
                + error != null ? error : "UNKNOWN ERROR");
    }
    
    //Netabelarni ispisi
    static void printCreateCityResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String id = extractFirst(xml, "idCity");
        System.out.println("STATUS: SUCCESS");
        System.out.println("CITY CREATED, ID: " + id);
    }
    
    static void printCreateUserResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String id = extractFirst(xml, "idUser");
        System.out.println("STATUS: SUCCESS");
        System.out.println("USER CREATED, ID: " + id);
    }
    
    static void printIncreaseBalanceResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String newBalance = extractFirst(xml, "newBalance");
        String idUser = extractFirst(xml, "idUser");
        System.out.println("STATUS: SUCCESS");
        System.out.println("FOR USER ID: " + idUser);
        System.out.println("INCREASED BALANCE TO: " + newBalance);
    }
    
    static void printUpdateAddressAndCityResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String newAddress = extractFirst(xml, "newAddress");
        String newCity = extractFirst(xml, "newCity");
        String idUser = extractFirst(xml, "idUser");
        System.out.println("STATUS: SUCCESS");
        System.out.println("FOR USER ID: " + idUser);
        System.out.println("ADDRESS UPDATED TO: " + newAddress);
        System.out.println("CITY UPDATED TO: " + newCity);
    }
    
    static void printCreateCategoryResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String id = extractFirst(xml, "idCategory");
        System.out.println("STATUS: SUCCESS");
        System.out.println("CATEGORY CREATED, ID: " + id);
    }
    
    static void printCreateProductResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String id = extractFirst(xml, "idProduct");
        System.out.println("STATUS: SUCCESS");
        System.out.println("PRODUCT CREATED, ID: " + id);
    }
    
    static void printUpdateProductPriceResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String newPrice = extractFirst(xml, "newPrice");
        System.out.println("STATUS: SUCCESS");
        System.out.println("PRICE UPDATED TO: " + newPrice);
    }
    
    static void printSetDiscountResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String newDiscount = extractFirst(xml, "newDiscount");
        System.out.println("STATUS: SUCCESS");
        System.out.println("DISCOUNT UPDATED TO: " + newDiscount);
    }
    
    static void printAddToCartResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String id = extractFirst(xml, "productId");
        String quantity = extractFirst(xml, "quantity");
        System.out.println("STATUS: SUCCESS");
        System.out.println("PRODUCT ID: " + id + ", QUANTITY: " + quantity);
    }
    
    static void printRemoveFromCartResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String id = extractFirst(xml, "productId");
        String quantity = extractFirst(xml, "quantity");
        System.out.println("STATUS: SUCCESS");
        System.out.println("PRODUCT ID: " + id + ", LEFT QUANTITY: " + quantity);
    }
    
    static void printAddToWishlistResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String id = extractFirst(xml, "id");
        System.out.println("STATUS: SUCCESS");
        System.out.println("WISHLIST ITEM ID: " + id + " ADDED TO WISHLIST");
    }
    
    static void printRemoveFromWishlistResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String id = extractFirst(xml, "id");
        System.out.println("STATUS: SUCCESS");
        System.out.println("WISHLIST ITEM ID: " + id + " REMOVED FROM WISHLIST");
    }
    
    static void printPayResponse(String xml){
        if(hasError(xml)){
            printError(xml);
            return;
        }
        String orderId = extractFirst(xml, "orderId");
        String transactionId = extractFirst(xml, "transactionId");
        String total = extractFirst(xml, "total");
        System.out.println("STATUS: SUCCESS");
        System.out.println("TRANSACTION MADE, ID: " + transactionId + " OF ORDER, ID: " + orderId);
        System.out.println("TOTAL: " + total);
    }

    //Ispis tabele
    static void printTable(String title, String[] headers, List<String[]> rows) {
        if (rows.isEmpty()) {
            System.out.println("[ " + title + " - NO DATA ]");
            return;
        }
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) widths[i] = headers[i].length();
        for (String[] row : rows)
            for (int i = 0; i < headers.length && i < row.length; i++)
                if (row[i] != null) widths[i] = Math.max(widths[i], row[i].length());

        String sep = buildSep(widths);
        System.out.println();
        System.out.println(" " + title);
        System.out.println(sep);
        System.out.println(buildRow(headers, widths));
        System.out.println(sep);
        for (String[] row : rows) System.out.println(buildRow(row, widths));
        System.out.println(sep);
        System.out.println(" " + rows.size() + " row(s)");
    }

    private static String buildSep(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : widths) { for (int i = 0; i < w + 2; i++) sb.append('-'); sb.append('+'); }
        return sb.toString();
    }

    private static String buildRow(String[] cells, int[] widths) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < widths.length; i++) {
            String cell = (i < cells.length && cells[i] != null) ? cells[i] : "";
            sb.append(' ').append(cell);
            for (int s = cell.length(); s < widths[i]; s++) sb.append(' ');
            sb.append(" |");
        }
        return sb.toString();
    }

    //Specificni ispisi za razlicite funkcionalnosti
    static void printUsers(String xml) {
        List<String> blocks = extractBlocks(xml, "user");
        String[] headers = {"ID", "Username", "First Name", "Last Name", "Role", "Balance", "Address", "City"};
        List<String[]> rows = new ArrayList<>();
        for (String b : blocks) {
            rows.add(new String[]{
                val(b, "idUser"), val(b, "username"), val(b, "firstName"), val(b, "lastName"),
                val(b, "roles"), val(b, "balance"), val(b, "address"), val(b, "city")
            });
        }
        printTable("USERS", headers, rows);
    }

    static void printCities(String xml) {
        List<String> blocks = extractBlocks(xml, "city");
        String[] headers = {"ID", "Name"};
        List<String[]> rows = new ArrayList<>();
        for (String b : blocks) rows.add(new String[]{ val(b, "id"), val(b, "name") });
        printTable("CITIES", headers, rows);
    }

    static void printCategories(String xml) {
        List<String> blocks = extractBlocks(xml, "category");
        String[] headers = {"ID", "Name", "Parent ID"};
        List<String[]> rows = new ArrayList<>();
        for (String b : blocks)
            rows.add(new String[]{ val(b, "id"), val(b, "name"), val(b, "parentId") });
        printTable("CATEGORIES", headers, rows);
    }

    static void printProducts(String xml) {
        List<String> blocks = extractBlocks(xml, "product");
        String[] headers = {"ID", "Name", "Description", "Price", "Discount", "Seller ID", "Category"};
        List<String[]> rows = new ArrayList<>();
        for (String b : blocks) {
            rows.add(new String[]{
                val(b, "id"), val(b, "name"), val(b, "description"),
                val(b, "price"), val(b, "discount"), val(b, "sellerId"), val(b, "categoryName")
            });
        }
        printTable("PRODUCTS", headers, rows);
    }

    static void printCart(String xml, String label) {
        List<String> blocks = extractBlocks(xml, "item");
        if (blocks.isEmpty()) blocks = extractBlocks(xml, "product");

        String[] headers = {"Product ID", "Name", "Price", "Discount%", "Qty"};
        List<String[]> rows = new ArrayList<>();
        for (String b : blocks) {
            rows.add(new String[]{
                val(b, "productId"), val(b, "productName"), val(b, "price"),
                val(b, "discount"), val(b, "quantity")
            });
        }
        printTable(label, headers, rows);
    }
    
    static void printWishlist(String xml, String label) {
        List<String> blocks = extractBlocks(xml, "item");
        if (blocks.isEmpty()) blocks = extractBlocks(xml, "product");

        String[] headers = {"Product ID", "Name", "Price", "Added when"};
        List<String[]> rows = new ArrayList<>();
        for (String b : blocks) {
            rows.add(new String[]{
                val(b, "productId"), val(b, "productName"), val(b, "price"),
                val(b, "addedWhen")
            });
        }
        printTable(label, headers, rows);
    }

    static void printOrders(String xml) {
        List<String> blocks = extractBlocks(xml, "order");
        String[] headers = {"Order ID", "User ID", "Address", "City ID", "Total price", "Date"};
        List<String[]> rows = new ArrayList<>();
        for (String b : blocks) {
            rows.add(new String[]{
                val(b, "id"), val(b, "userId"), val(b, "address"),
                val(b, "cityId"), val(b, "totalPrice"), val(b, "createdWhen")
            });
        }
        printTable("ORDERS", headers, rows);
    }

    static void printTransactions(String xml) {
        List<String> blocks = extractBlocks(xml, "transaction");
        String[] headers = {"Transaction ID", "User ID", "Order ID", "Amount", "Date"};
        List<String[]> rows = new ArrayList<>();
        for (String b : blocks) {
            rows.add(new String[]{
                val(b, "idTransaction"), val(b, "userId"), val(b, "orderId"),
                val(b, "moneyPaid"), val(b, "createdWhen")
            });
        }
        printTable("TRANSACTIONS", headers, rows);
    }

    static void printSimpleResponse(String xml) {
        
        String msg    = extractFirst(xml, "message", "status", "error", "result", "description");
        String status = extractFirst(xml, "status", "success", "code");
        if (msg != null || status != null) {
            if (status != null) System.out.printf("STATUS : " + status);
            if (msg != null) System.out.printf("MESSAGE : " + msg);
        } else {
            
            System.out.println(xml.trim());
        }
    }

    private static String extractFirst(String xml, String... tags) {
        for (String tag : tags) {
            String open = "<" + tag + ">", close = "</" + tag + ">";
            int i1 = xml.indexOf(open);
            if (i1 == -1) continue;
            int i2 = xml.indexOf(close, i1 + open.length());
            if (i2 == -1) continue;
            String v = xml.substring(i1 + open.length(), i2).trim();
            if (!v.isEmpty()) return v;
        }
        return null;
    }
}
