package com.uranusdb.actions.node;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.uranusdb.events.ExchangeEvent;
import com.uranusdb.server.Constants;
import com.uranusdb.server.Types;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.util.HashMap;

import static com.uranusdb.server.UranusServer.graphs;

public interface PostNode {

    static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        String body = exchangeEvent.getBody();
        String id = exchangeEvent.getParameters().get(Constants.ID);
        String label = exchangeEvent.getParameters().get(Constants.LABEL);
        if (body.isEmpty()) {
                graphs[number].addNode(label, id);
        } else {
            HashMap<String, Object> properties = JsonIterator.deserialize(body, Types.MAP);
            graphs[number].addNode(label, id, properties);
        }
        if (respond) {
            HttpServerExchange exchange = exchangeEvent.get();
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.setStatusCode(StatusCodes.CREATED);
            exchange.getResponseSender().send(
                    JsonStream.serialize(Types.MAP, graphs[number].getNode(label, id)));
            exchangeEvent.clear();
        }
    }
}
