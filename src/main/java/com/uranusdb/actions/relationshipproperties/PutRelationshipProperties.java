package com.uranusdb.actions.relationshipproperties;

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

public interface PutRelationshipProperties {

    static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        HttpServerExchange exchange = exchangeEvent.get();
        String body = exchangeEvent.getBody();
        Map<String, String> parameters = exchangeEvent.getParameters();
        boolean succeeded;

        if (getRelationship(number, parameters) == null) {
            if (respond) {
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
                exchangeEvent.clear();
            }
            return;
        }

        HashMap<String, Object> properties;
        if (body.isEmpty()) {
            properties = new HashMap<>();
        } else {
            properties = JsonIterator.deserialize(body, Types.MAP);
        }

        succeeded = updateRelationshipProperties(number, parameters, properties);

        if (respond) {
            if (succeeded) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.setStatusCode(StatusCodes.CREATED);
                exchange.getResponseSender().send(
                        JsonStream.serialize(Types.MAP, getRelationship(number, parameters)));
            } else {
                exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
            }
            exchangeEvent.clear();
        }

    }

    static boolean updateRelationshipProperties(int number, Map<String, String> parameters, HashMap<String, Object> properties) {
        boolean succeeded;

        if(parameters.containsKey(Constants.NUMBER)) {
            succeeded = graphs[number].updateRelationshipProperties(parameters.get(Constants.TYPE),
                    parameters.get(Constants.LABEL1),
                    parameters.get(Constants.FROM),
                    parameters.get(Constants.LABEL2),
                    parameters.get(Constants.TO),
                    Integer.parseInt(parameters.get(Constants.NUMBER)),
                    properties);
        } else {

            succeeded = graphs[number].updateRelationshipProperties(parameters.get(Constants.TYPE),
                    parameters.get(Constants.LABEL1),
                    parameters.get(Constants.FROM),
                    parameters.get(Constants.LABEL2),
                    parameters.get(Constants.TO),
                    properties);
        }
        return succeeded;
    }

    static Map<String, Object> getRelationship(int number, Map<String, String> parameters) {
        Map<String, Object> relationship;
        if(parameters.containsKey(Constants.NUMBER)) {
            relationship = graphs[number].getRelationship(
                    parameters.get(Constants.TYPE),
                    parameters.get(Constants.LABEL1),
                    parameters.get(Constants.FROM),
                    parameters.get(Constants.LABEL2),
                    parameters.get(Constants.TO),
                    Integer.parseInt(parameters.get(Constants.NUMBER)));
        } else {
            relationship = graphs[number].getRelationship(
                    parameters.get(Constants.TYPE),
                    parameters.get(Constants.LABEL1),
                    parameters.get(Constants.FROM),
                    parameters.get(Constants.LABEL2),
                    parameters.get(Constants.TO));
        }
        return relationship;
    }

}
