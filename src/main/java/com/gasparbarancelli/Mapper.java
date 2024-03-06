package com.gasparbarancelli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Mapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Mapper() {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    Transacao map(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, Transacao.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String map(Cliente cliente) {
        try {
            return OBJECT_MAPPER.writeValueAsString(cliente);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    String map(Extrato extrato) {
        try {
            return OBJECT_MAPPER.writeValueAsString(extrato);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
