package com.uranusdb.actions.relationship;

import com.uranusdb.events.ExchangeEvent;
import com.uranusdb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.util.Map;

import static com.uranusdb.server.UranusServer.graphs;

public interface DeleteRelationship {

    static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        HttpServerExchange exchange = exchangeEvent.get();
        Map<String, String> parameters = exchangeEvent.getParameters();

        boolean succeeded;

        if(parameters.containsKey(Constants.NUMBER)) {
                succeeded = graphs[number].removeRelationship(parameters.get(Constants.TYPE),
                        exchangeEvent.getParameters().get(Constants.LABEL1),
                        parameters.get(Constants.FROM),
                        exchangeEvent.getParameters().get(Constants.LABEL2),
                        parameters.get(Constants.TO),
                        Integer.parseInt(parameters.get(Constants.NUMBER)));

        } else {
                succeeded = graphs[number].removeRelationship(parameters.get(Constants.TYPE),
                        exchangeEvent.getParameters().get(Constants.LABEL1),
                        parameters.get(Constants.FROM),
                        exchangeEvent.getParameters().get(Constants.LABEL2),
                        parameters.get(Constants.TO));

        }
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
