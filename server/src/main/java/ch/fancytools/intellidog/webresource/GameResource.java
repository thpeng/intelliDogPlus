/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.fancytools.intellidog.webresource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author caliban
 */
@Path("/game")
public class GameResource {
    
    
    @GET
    @Path("move/{id}/{card}/{from}/{to}")
    public void move(@PathParam("id") String id, @PathParam("card") int card, @PathParam("from") int from, @PathParam("to") int to)
    {
        
    }
    
}
