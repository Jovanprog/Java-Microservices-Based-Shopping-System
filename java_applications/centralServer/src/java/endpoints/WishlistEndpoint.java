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

@Path("wishlist")
@Stateless
public class WishlistEndpoint {

    @EJB
    JMSService jmsService;

    // 12. Dodavanje artikla u listu želja
    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response addToWishlist(String xml) {
        try {
            RequestMessage message = new RequestMessage("ADD_TO_WISHLIST", xml);
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

    // 13. Brisanje artikla iz liste želja
    @PUT
    @Path("remove")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response removeFromWishlist(String xml) {
        try {
            RequestMessage message = new RequestMessage("REMOVE_FROM_WISHLIST", xml);
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

    // 20. Dohvatanje sadržaja liste želja
    @POST
    @Path("get")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response getWishlist(String xml) {
        try {
            RequestMessage message = new RequestMessage("GET_WISHLIST", xml);
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

