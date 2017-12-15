package com.uranusdb.actions.relationship;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.uranusdb.events.ExchangeEvent;
import com.uranusdb.server.Constants;
import com.uranusdb.server.Types;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.util.HashMap;
import java.util.Map;

import static com.uranusdb.server.UranusServer.graphs;

public interface PostRelationship {

    static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        HttpServerExchange exchange = exchangeEvent.get();
        Map<String, String> parameters = exchangeEvent.getParameters();
        String body = exchangeEvent.getBody();

        if (body.isEmpty()) {

            graphs[number].addRelationship(parameters.get(Constants.TYPE),
                    exchangeEvent.getParameters().get(Constants.LABEL1),
                    parameters.get(Constants.FROM),
                    exchangeEvent.getParameters().get(Constants.LABEL2),
                    parameters.get(Constants.TO));

            if (respond) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.setStatusCode(StatusCodes.CREATED);
                exchange.getResponseSender().send(
                        JsonStream.serialize(Types.MAP,
                                graphs[number].getRelationship(parameters.get(Constants.TYPE),
                                        exchangeEvent.getParameters().get(Constants.LABEL1),
                                        parameters.get(Constants.FROM),
                                        exchangeEvent.getParameters().get(Constants.LABEL2),
                                        parameters.get(Constants.TO))));
                exchangeEvent.clear();
            }
        } else {

                HashMap<String, Object> properties = JsonIterator.deserialize(body, Types.MAP);
                graphs[number].addRelationship(parameters.get(Constants.TYPE),
                        exchangeEvent.getParameters().get(Constants.LABEL1),
                        parameters.get(Constants.FROM),
                        exchangeEvent.getParameters().get(Constants.LABEL2),
                        parameters.get(Constants.TO), properties);

            if (respond) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.setStatusCode(StatusCodes.CREATED);
                exchange.getResponseSender().send(
                        JsonStream.serialize(Types.MAP,
                                graphs[number].getRelationship(parameters.get(Constants.TYPE),
                                        exchangeEvent.getParameters().get(Constants.LABEL1),
                                        parameters.get(Constants.FROM),
                                        exchangeEvent.getParameters().get(Constants.LABEL2),
                                        parameters.get(Constants.TO))));
                exchangeEvent.clear();
            }
        }

    }
}
