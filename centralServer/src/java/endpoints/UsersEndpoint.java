/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoints;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import jmsServices.JMSService;
import jmsServices.RequestMessage;

/**
 *
 * @author Jovan
 */
@Path("users")
@Stateless
public class UsersEndpoint {
    @EJB
    JMSService jmsService;
    
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response login(String requestMessage) {
        try {

            RequestMessage message = new RequestMessage("LOGIN", requestMessage);

            String response = jmsService.sendAndReceiveSubsystem1(message);

            if (response.trim().startsWith("<error")) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
            }
            return Response.ok(response).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>" + e.getMessage() + "</error>").build();
        }
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response createUser(String requestMessage) {
        try {
            RequestMessage message = new RequestMessage("CREATE_USER", requestMessage);
            String response = jmsService.sendAndReceiveSubsystem1(message);

            if (response.trim().startsWith("<error")) {
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>" + e.getMessage() + "</error>").build();
        }
    }
    
    @PUT
    @Path("increaseBalance")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response increaseBalance(String requestMessage) {
        try {
            RequestMessage message = new RequestMessage("INCREASE_BALANCE", requestMessage);
            String response = jmsService.sendAndReceiveSubsystem1(message);

            if (response.trim().startsWith("<error")) {
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>" + e.getMessage() + "</error>").build();
        }
    }
    
    @PUT
    @Path("updateAddress")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response updateAddress(String requestMessage) {
        try {
            RequestMessage message = new RequestMessage("UPDATE_USER_CITY_AND_ADDRESS", requestMessage);
            String response = jmsService.sendAndReceiveSubsystem1(message);

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
    public Response getAllUsers() {
        try {
            
            RequestMessage message = new RequestMessage("GET_ALL_USERS", "");
            String response = jmsService.sendAndReceiveSubsystem1(message);

            if (response.trim().startsWith("<error")) {
                System.out.println("lose");
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("<error>" + e.getMessage() + "</error>").build();
        }
    }
}
