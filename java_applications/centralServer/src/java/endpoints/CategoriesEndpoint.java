package endpoints;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import jmsServices.JMSService;
import jmsServices.RequestMessage;

@Path("categories")
@Stateless
public class CategoriesEndpoint {

    @EJB
    JMSService jmsService;

    // 6. Kreiranje kategorije
    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response createCategory(String xml) {
        try {
            RequestMessage message = new RequestMessage("CREATE_CATEGORY", xml);
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

    // 17. Dohvatanje svih kategorija
    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_XML)
    public Response getAllCategories() {
        try {
            RequestMessage message = new RequestMessage("GET_ALL_CATEGORIES", "");
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

