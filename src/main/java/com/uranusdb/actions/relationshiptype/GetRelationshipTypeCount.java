package com.uranusdb.actions.relationshiptype;

import com.jsoniter.output.JsonStream;
import com.uranusdb.events.ExchangeEvent;
import com.uranusdb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import static com.uranusdb.server.UranusServer.graphs;

public interface GetRelationshipTypeCount {

    static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(
                JsonStream.serialize(
                        graphs[number].getRelationshipTypeCount(exchangeEvent.getParameters().get(Constants.TYPE))));
        exchangeEvent.clear();
    }
}
