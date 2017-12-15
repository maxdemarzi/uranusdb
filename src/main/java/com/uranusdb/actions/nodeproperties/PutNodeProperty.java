package com.uranusdb.actions.nodeproperties;

import com.jsoniter.JsonIterator;
import com.uranusdb.events.ExchangeEvent;
import com.uranusdb.server.Constants;
import com.uranusdb.server.Types;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import static com.uranusdb.server.UranusServer.graphs;

public interface PutNodeProperty {

    static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        HttpServerExchange exchange = exchangeEvent.get();
        String body = exchangeEvent.getBody();
        boolean succeeded ;
        if (body.isEmpty()) {
            if(respond) {
                exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
                exchangeEvent.clear();
            }
        } else {
            Object property = JsonIterator.deserialize(body, Types.OBJECT);
            succeeded = graphs[number].updateNodeProperty(
                    exchangeEvent.getParameters().get(Constants.LABEL),
                    exchangeEvent.getParameters().get(Constants.ID),
                    exchangeEvent.getParameters().get(Constants.KEY),
                    property);
            if (respond) {
                if (succeeded) {
                    exchange.setStatusCode(StatusCodes.NO_CONTENT);
                } else {
                    exchange.setStatusCode(StatusCodes.NOT_FOUND);
                }
                exchangeEvent.clear();
            }
        }
    }
}
