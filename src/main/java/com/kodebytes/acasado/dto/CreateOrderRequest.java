package com.kodebytes.acasado.dto;

import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotNull String customerId,
        BigDecimal amount
) {
}

