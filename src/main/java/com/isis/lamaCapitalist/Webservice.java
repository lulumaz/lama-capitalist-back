package com.isis.lamaCapitalist;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lmazel
 */





import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("generic")
public class Webservice {
    Services services;
    public Webservice() {
        services = new Services();
    }
    
    /*@GET
    @Path("world")
    @Produces(MediaType.APPLICATION_XML)
    public Response getWorld() {
        return Response.ok(services.readWorldFromXml()).build();
    }*/
    @GET
    @Path("world")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getXml(@Context HttpServletRequest request) {
        String username = request.getHeader("X-user");
        if(username!=null){
            return Response.ok(services.readWorldFromXml(username)).build();
        } else {
           return Response.ok(services.readWorldFromXml()).build();
        }
    }
}
