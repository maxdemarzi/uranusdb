package com.uranusdb.actions.nodeproperties;

import com.uranusdb.events.ExchangeEvent;
import com.uranusdb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import static com.uranusdb.server.UranusServer.graphs;

public interface DeleteNodeProperty {

    static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        boolean succeeded = graphs[number].deleteNodeProperty(exchangeEvent.getParameters().get(Constants.LABEL), exchangeEvent.getParameters().get(Constants.ID),
                    exchangeEvent.getParameters().get(Constants.KEY));
        if (respond) {
            HttpServerExchange exchange = exchangeEvent.get();
            if (succeeded) {
                exchange.setStatusCode(StatusCodes.NO_CONTENT);
            } else {
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
            }
            exchangeEvent.clear();
        }
    }
}
