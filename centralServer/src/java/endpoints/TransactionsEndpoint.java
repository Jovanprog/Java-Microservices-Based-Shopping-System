package endpoints;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import jmsServices.JMSService;
import jmsServices.RequestMessage;

@Path("transactions")
@Stateless
public class TransactionsEndpoint {

    @EJB
    JMSService jmsService;

    // 23. Sve transakcije
    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_XML)
    public Response getAllTransactions() {
        try {
            RequestMessage message = new RequestMessage("GET_ALL_TRANSACTIONS", "");
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
}

