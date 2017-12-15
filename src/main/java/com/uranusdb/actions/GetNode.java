package com.uranusdb.actions;

import com.jsoniter.output.JsonStream;
import com.uranusdb.events.ExchangeEvent;
import com.uranusdb.server.Constants;
import com.uranusdb.server.Types;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.util.Map;

import static com.uranusdb.server.UranusServer.graphs;

public interface GetNode {

    static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();

        Map<String, Object> node = graphs[number].getNode(exchangeEvent.getParameters().get(Constants.LABEL), exchangeEvent.getParameters().get(Constants.ID));
        if (node == null) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
        } else {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(
                    JsonStream.serialize(Types.MAP, node));
        }
        exchangeEvent.clear();
    }

}
