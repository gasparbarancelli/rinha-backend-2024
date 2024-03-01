package io.github.gasparbarancelli;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.ENTITY_CODER)
public class ThrowableMapper implements ExceptionMapper<ValueInstantiationException> {

    @Override
    public Response toResponse(ValueInstantiationException exception) {
        return Response.status(422).build();
    }

}