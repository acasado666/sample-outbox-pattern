package com.kodebytes.acasado.order;

import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotNull String customerId,
        BigDecimal amount
) {
}

