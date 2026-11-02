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

@Path("cart")
@Stateless
public class CartEndpoint {

    @EJB
    JMSService jmsService;

    // 10. Dodavanje u korpu
    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response addToCart(String xml) {
        try {
            RequestMessage message = new RequestMessage("ADD_TO_CART", xml);
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

    // 11. Brisanje iz korpe
    @PUT
    @Path("remove")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response removeFromCart(String xml) {
        try {
            RequestMessage message = new RequestMessage("REMOVE_FROM_CART", xml);
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

    // 19. Dohvatanje sadržaja korpe
    @POST
    @Path("get")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response getCart(String xml) {
        try {
            RequestMessage message = new RequestMessage("GET_CART", xml);
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

