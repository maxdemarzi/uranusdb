package com.uranusdb.server;

import com.uranusdb.actions.Action;
import com.uranusdb.events.ExchangeEvent;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;

public class RequestHandler implements HttpHandler {
    private boolean write;
    private Action action;

    RequestHandler(boolean write, Action action) {
        this.write = write;
        this.action = action;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {

        final long seq = UranusServer.ringBuffer.next();
        final ExchangeEvent exchangeEvent = UranusServer.ringBuffer.get(seq);

        exchangeEvent.setRequest(write, action, exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY).getParameters());

        exchangeEvent.set(exchange);
        UranusServer.ringBuffer.publish(seq);
        // This is deprecated but it works...
        exchange.dispatch();
    }
}