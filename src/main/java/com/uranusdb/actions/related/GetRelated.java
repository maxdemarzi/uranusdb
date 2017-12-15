package com.uranusdb.actions.related;

import com.uranusdb.events.ExchangeEvent;
import com.uranusdb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.util.Map;

import static com.uranusdb.server.UranusServer.graphs;

public interface GetRelated {

    static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();
        Map<String, String> parameters = exchangeEvent.getParameters();
        boolean exists;

            exists = graphs[number].related(exchangeEvent.getParameters().get(Constants.LABEL1),
                    parameters.get(Constants.FROM),
                    exchangeEvent.getParameters().get(Constants.LABEL2),
                    parameters.get(Constants.TO));

        if (exists) {
            exchange.setStatusCode(StatusCodes.OK);
        } else {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
        }
        exchangeEvent.clear();
    }
}
