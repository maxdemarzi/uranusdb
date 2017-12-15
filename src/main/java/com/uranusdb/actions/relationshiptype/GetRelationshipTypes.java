package com.uranusdb.actions.relationshiptype;

import com.jsoniter.output.JsonStream;
import com.uranusdb.events.ExchangeEvent;
import com.uranusdb.server.Types;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import static com.uranusdb.server.UranusServer.graphs;

public interface GetRelationshipTypes {

    static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(
                JsonStream.serialize(Types.SET, graphs[number].getRelationshipTypes()));
        exchangeEvent.clear();
    }
}
