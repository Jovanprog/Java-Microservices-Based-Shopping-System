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
@Path("cities")
@Stateless
public class CitiesEndpoint {
    
    @EJB
    JMSService jmsService;
    
    //------------------Kreiranje grada--------------------
    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response createCity(String requestMessage) {
        try {

            RequestMessage message = new RequestMessage("CREATE_CITY", requestMessage);

            String response = jmsService.sendAndReceiveSubsystem1(message);
            System.out.println("RESPONSE: " + response);
            if (response.trim().startsWith("<error")) {
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("<error>" + e.getMessage() + "</error>").build();
        }
    }
    
    //--------------------------Dohvati sve gradove----------------------------
    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_XML)
    public Response getAllCities() {
        try {

            RequestMessage message = new RequestMessage("GET_ALL_CITIES", "");

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
}
