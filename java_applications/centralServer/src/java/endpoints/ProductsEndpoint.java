package endpoints;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import jmsServices.JMSService;
import jmsServices.RequestMessage;

@Path("products")
@Stateless
public class ProductsEndpoint {

    @EJB
    JMSService jmsService;

    // 7. Kreiranje artikla
    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response createProduct(String xml) {
        try {
            RequestMessage message = new RequestMessage("CREATE_PRODUCT", xml);
            String response = jmsService.sendAndReceiveSubsystem2(message);

            if (response.trim().startsWith("<error")) {
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>" + e.getMessage() + "</error>").build();
        }
    }

    // 8. Menjanje cene artikla
    @PUT
    @Path("price")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response updatePrice(String xml) {
        try {
            RequestMessage message = new RequestMessage("UPDATE_PRODUCT_PRICE", xml);
            String response = jmsService.sendAndReceiveSubsystem2(message);

            if (response.trim().startsWith("<error")) {
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>" + e.getMessage() + "</error>").build();
        }
    }

    // 9. Postavljanje popusta
    @PUT
    @Path("discount")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response setDiscount(String xml) {
        try {
            RequestMessage message = new RequestMessage("SET_DISCOUNT", xml);
            String response = jmsService.sendAndReceiveSubsystem2(message);

            if (response.trim().startsWith("<error")) {
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>" + e.getMessage() + "</error>").build();
        }
    }

    // 18. Dohvatanje artikala koje prodaje korisnik
    @POST
    @Path("bySeller")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response getProductsBySeller(String xml) {
        try {
            RequestMessage message = new RequestMessage("GET_SELLER_PRODUCTS", xml);
            String response = jmsService.sendAndReceiveSubsystem2(message);

            if (response.trim().startsWith("<error")) {
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>" + e.getMessage() + "</error>").build();
        }
    }
}

