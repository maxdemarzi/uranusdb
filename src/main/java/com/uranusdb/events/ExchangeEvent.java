package com.uranusdb.events;

import com.uranusdb.actions.Action;
import io.undertow.server.HttpServerExchange;

import java.util.Map;

public class ExchangeEvent {

    private HttpServerExchange exchange;
    private boolean write;
    private Action action;
    private Map<String, String> parameters;
    private String body;
    private int responder;

    public void set(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    public HttpServerExchange get() {
        return this.exchange;
    }

    public void setRequest(boolean write, Action action, Map<String, String> parameters) {
        this.write = write;
        this.action = action;
        this.parameters = parameters;
    }

    public void setResponder(int responder) {
        this.responder = responder;
    }

    public boolean isResponder(int responder) {
        return this.responder == responder;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Action getAction() {
        return action;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getBody() {
        return body;
    }

    public boolean getWrite() {
        return write;
    }

    public void clear() {
        exchange.endExchange();
        exchange = null;
    }
}