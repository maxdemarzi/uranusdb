package com.uranusdb.actions.node;

import com.uranusdb.events.ExchangeEvent;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

public interface NoOp {

    static void handle(ExchangeEvent exchangeEvent) {
        HttpServerExchange exchange = exchangeEvent.get();
        exchange.setStatusCode(StatusCodes.OK);
        exchangeEvent.clear();
    }

}
