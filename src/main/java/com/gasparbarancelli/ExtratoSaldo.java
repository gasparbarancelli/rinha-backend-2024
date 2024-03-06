package com.gasparbarancelli;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ExtratoSaldo(
            int total,
            @JsonProperty("data_extrato")
            @JsonFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
            LocalDateTime data,
            int limite) {

}