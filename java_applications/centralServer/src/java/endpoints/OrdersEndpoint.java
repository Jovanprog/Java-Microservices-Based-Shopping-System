package endpoints;

import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import jmsServices.JMSService;
import jmsServices.RequestMessage;

@Path("orders")
@Stateless
public class OrdersEndpoint {

    @EJB
    JMSService jmsService;

    @POST
    @Path("pay")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Response pay(String xml) {
        try {
            //Dohvatanje user-a
            int userId = extractIntTag(xml, "userId");
            if(userId < 0){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("<error>MISSING USER ID</error>").build();
            }
            
            //System.out.println("User ID: " + userId);
            
            //Dohvatanje cart-a
            String cartXml = "<cartRequest><userId>" + userId + "</userId></cartRequest>";
            String cartResponse = jmsService.sendAndReceiveSubsystem2(
            new RequestMessage("GET_CART", cartXml));
            
            //System.out.println("cartResponse: " + cartResponse);
            
            if(cartResponse.trim().startsWith("<error>")){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("<error>COULD NOT GET CART</error>").build();
            }
            
            //Dohvatanje ukupne cene cart-a
            BigDecimal total = calculateCartTotal(cartResponse);
            if(total.compareTo(BigDecimal.ZERO) == 0){
             return Response.status(Response.Status.BAD_REQUEST)
                        .entity("<error>CART IS EMPTY</error>").build();
            }
            //System.out.println("Total: " + total);
            //Dohvatanje balansa user-a
            String balanceXml = "<balanceRequest><userId>" + userId + "</userId></balanceRequest>";
            String balanceResponse = jmsService.sendAndReceiveSubsystem1(
                    new RequestMessage("GET_BALANCE", balanceXml));
            
            //System.out.println("balanceResponse: " + balanceResponse);
            
            if(balanceResponse.trim().startsWith("<error>")){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("<error>COULD NOT GET BALANCE</error>").build();
            }
            
            
            
            
            //Provera da li ima dovoljno novca
            BigDecimal balance = extractDecimalTag(balanceResponse, "balance");
            if(balance.compareTo(total) < 0){
             return Response.status(Response.Status.BAD_REQUEST)
                        .entity("<error>INSUFFICINET FUNDS</error>").build();
            }
            
            //Provera da li postoji trazeni city
            String cityId = extractStringTag(xml, "cityId");

            // Provera cityId
            String cityCheckXml = "<cityRequest><cityId>" + cityId + "</cityId></cityRequest>";
            String cityResponse = jmsService.sendAndReceiveSubsystem1(
                    new RequestMessage("GET_CITY", cityCheckXml));

            if (cityResponse.trim().startsWith("<error>")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("<error>INVALID CITY</error>").build();
            }
            
            //Pravljenje XML-a sa stavkama iz cart-a
            String address = extractStringTag(xml, "address");
            //String cityId = extractStringTag(xml, "cityId");
            String payXml = buildPayXml(userId, address, cityId, cartResponse);
            
            //Posalji request subsystem-u 3 da kreira order i transakciju
            String payResponse = jmsService.sendAndReceiveSubsystem3(
                    new RequestMessage("PAY", payXml));
            
            //System.out.println("payResponse: " + payResponse);
            if(payResponse.trim().startsWith("<error>")){
                //payResponse se dobija iz subsystem-a 3
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(payResponse).build();
            }
            
            //Smanji balans u subsystem-u 1
            String decreaseXml = "<decreaseBalanceRequest><userId>" + userId + "</userId>" +
                    "<amount>" + total.toPlainString() + "</amount></decreaseBalanceRequest>";
            String decreaseResponse = jmsService.sendAndReceiveSubsystem1(
                    new RequestMessage("DECREASE_BALANCE", decreaseXml));
            //System.out.println("decreaseResponse: " + decreaseResponse);
            
            if(decreaseResponse.trim().startsWith("<error>")){
                //payResponse se dobija iz subsystem-a 3
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("<error>COULD NOT DECREASE BALANCE</error>").build();
            }
            
            //Brisanje proizvoda iz cart-a user-a
            String clearXml = "<clearCartRequest><userId>" + userId + "</userId></clearCartRequest>";
            String clearResponse = jmsService.sendAndReceiveSubsystem2(
                    new RequestMessage("CLEAR_CART", clearXml));
            
            System.out.println("clearResponse: " + clearResponse);
            if(clearResponse.trim().startsWith("<error>")){
                //payResponse se dobija iz subsystem-a 3
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(clearResponse).build();
            }
            
            return Response.ok(payResponse).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>" + e.getMessage() + "</error>").build();
        }
    }

    @POST
    @Path("user")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response getUserOrders(String xml) {
        try {
            RequestMessage message = new RequestMessage("GET_USER_ORDERS", xml);
            String response = jmsService.sendAndReceiveSubsystem3(message);

            if (response.trim().startsWith("<error")) {
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>" + e.getMessage() + "</error>").build();
        }
    }

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_XML)
    public Response getAllOrders() {
        try {
            RequestMessage message = new RequestMessage("GET_ALL_ORDERS", "");
            String response = jmsService.sendAndReceiveSubsystem3(message);

            if (response.trim().startsWith("<error")) {
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>" + e.getMessage() + "</error>").build();
        }
    }
    
    //Pomocne funcije za placanje
    private BigDecimal calculateCartTotal(String cartXml){
        BigDecimal total = BigDecimal.ZERO;
        int pos = 0;
        while(true){
            int start = cartXml.indexOf("<item>", pos);
            if(start == -1) break;
            int end = cartXml.indexOf("</item>", pos);
            if(end == -1) break;
            String item = cartXml.substring(start + 6, end);
            
            String priceStr = extractStringTag(item, "price");
            String discountStr = extractStringTag(item, "discount");
            String quantityStr = extractStringTag(item, "quantity");
            
            if(priceStr != null && quantityStr != null){
                BigDecimal price = new BigDecimal(priceStr);
                int discount = (discountStr != null && !discountStr.isEmpty()) ? Integer.parseInt(discountStr) : 0;
                int quantity = Integer.parseInt(quantityStr);
                
                //Primena popusta
                BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                BigDecimal.valueOf(discount).divide(BigDecimal.valueOf(100)));
                BigDecimal finalPrice = price.multiply(discountMultiplier).multiply(BigDecimal.valueOf(quantity));
                total = total.add(finalPrice);
            }
            pos = end + 7;
        }
        return total;
    }
    
    private String buildPayXml(int userId, String address, String cityId, String cartXml){
        StringBuilder xml = new StringBuilder("<payRequest>");
        xml.append("<userId>").append(userId).append("</userId>");
        xml.append("<address>").append(address).append("</address>");
        xml.append("<cityId>").append(cityId).append("</cityId>");
        xml.append("<items>");
        
        int pos = 0;
        while (true){
            int start = cartXml.indexOf("<item>", pos);
            if(start == -1) break;
            int end = cartXml.indexOf("</item>", start);
            if(end == -1) break;
            String item = cartXml.substring(start + 6, end);
            
            String productId = extractStringTag(item, "productId");
            String quantity = extractStringTag(item, "quantity");
            String price = extractStringTag(item, "price");
            String discountStr = extractStringTag(item, "discount");
            
            int discount = (discountStr != null && !discountStr.isEmpty()) ? Integer.parseInt(discountStr) : 0;
            BigDecimal unitPrice = new BigDecimal(price);
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                BigDecimal.valueOf(discount).divide(BigDecimal.valueOf(100)));
            BigDecimal finalPrice = unitPrice.multiply(discountMultiplier);
            
            xml.append("<item>");
            xml.append("<productId>").append(productId).append("</productId>");
            xml.append("<quantity>").append(quantity).append("</quantity>");
            xml.append("<unitPrice>").append(finalPrice.toPlainString()).append("</unitPrice>");
            xml.append("</item>");
            pos = end + 7;
        }
        
        xml.append("</items></payRequest>");
        return xml.toString();
    }
    
    //Pomocne funkcije za izvlacenje tagova
    private int extractIntTag(String xml, String tag){
        String open = "<" + tag + ">", close = "</" + tag + ">";
        int i1 = xml.indexOf(open), i2 = xml.indexOf(close, i1 + open.length());
        if(i1 == -1 || i2 == -1) return -1;
        try{ return Integer.parseInt(xml.substring(i1 + open.length(), i2).trim()); }
        catch(NumberFormatException e){ return -1; }
    }
    
    private String extractStringTag(String xml, String tag){
        String open = "<" + tag + ">", close = "</" + tag + ">";
        int i1 = xml.indexOf(open), i2 = xml.indexOf(close, i1 + open.length());
        if(i1 == -1 || i2 == -1) return null;
        return xml.substring(i1 + open.length(), i2).trim();
    }
    
    private BigDecimal extractDecimalTag(String xml, String tag){
        String val = extractStringTag(xml, tag);
        if(val == null) return BigDecimal.ZERO;
        try{ return new BigDecimal(val); }
        catch(NumberFormatException e){ return BigDecimal.ZERO; }
    }
}

