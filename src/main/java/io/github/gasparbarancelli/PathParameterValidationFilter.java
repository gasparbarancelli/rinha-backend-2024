package io.github.gasparbarancelli;


import jakarta.annotation.Priority;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class PathParameterValidationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws WebApplicationException {
        String parametro = requestContext.getUriInfo().getPathParameters().getFirst("id");
        var id = Integer.parseInt(parametro);
        if (Cliente.naoExiste(id)) {
            throw new NotFoundException();
        }
    }

}