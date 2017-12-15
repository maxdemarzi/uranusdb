package com.uranusdb.actions.related;

import com.uranusdb.events.ExchangeEvent;
import com.uranusdb.graph.Direction;
import com.uranusdb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.util.Map;

import static com.uranusdb.server.UranusServer.graphs;

public interface GetRelatedByType {
    static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();
        Map<String, String> parameters = exchangeEvent.getParameters();
        boolean exists;

        exists = graphs[number].related(parameters.get(Constants.LABEL1),
                parameters.get(Constants.FROM),
                parameters.get(Constants.LABEL2),
                parameters.get(Constants.TO), Direction.OUT, parameters.get(Constants.TYPE));

        if (exists) {
            exchange.setStatusCode(StatusCodes.OK);
        } else {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
        }
        exchangeEvent.clear();
    }
}
