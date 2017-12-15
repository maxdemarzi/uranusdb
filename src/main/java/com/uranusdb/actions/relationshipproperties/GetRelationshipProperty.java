package com.uranusdb.actions.relationshipproperties;

import com.jsoniter.output.JsonStream;
import com.uranusdb.events.ExchangeEvent;
import com.uranusdb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.util.Map;

import static com.uranusdb.server.UranusServer.graphs;

public interface GetRelationshipProperty {

    static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();
        Map<String, String> parameters = exchangeEvent.getParameters();

        Object property;
        if(parameters.containsKey(Constants.NUMBER)) {
            property = graphs[number].getRelationshipProperty(
                    parameters.get(Constants.TYPE),
                    exchangeEvent.getParameters().get(Constants.LABEL1),
                    parameters.get(Constants.FROM),
                    exchangeEvent.getParameters().get(Constants.LABEL2),
                    parameters.get(Constants.TO),
                    Integer.parseInt(parameters.get(Constants.NUMBER)),
                    parameters.get(Constants.KEY));
        } else {
            property = graphs[number].getRelationshipProperty(
                    parameters.get(Constants.TYPE),
                    exchangeEvent.getParameters().get(Constants.LABEL1),
                    parameters.get(Constants.FROM),
                    exchangeEvent.getParameters().get(Constants.LABEL2),
                    parameters.get(Constants.TO),
                    parameters.get(Constants.KEY));
        }

        if (property == null) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
        } else {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(
                    JsonStream.serialize(property));
        }
        exchangeEvent.clear();
    }
}
