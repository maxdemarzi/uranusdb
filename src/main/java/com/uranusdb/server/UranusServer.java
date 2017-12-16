package com.uranusdb.server;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.uranusdb.actions.Action;
import com.uranusdb.events.DatabaseEventHandler;
import com.uranusdb.events.ExchangeEvent;
import com.uranusdb.events.PersistenceHandler;
import com.uranusdb.graph.FastUtilGraph;
import com.uranusdb.graph.Graph;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.util.StatusCodes;

import javax.servlet.ServletException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.uranusdb.server.Constants.*;

public class UranusServer {

    private static Undertow undertow;
    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    public static final Graph[] graphs = new Graph[Runtime.getRuntime().availableProcessors()];
    static RingBuffer<ExchangeEvent> ringBuffer;

    public UranusServer() {
        Config conf = ConfigFactory.load("uranus");
        new UranusServer(conf);
    }

    public UranusServer(Config conf) {
        for (int i = -1; ++i < graphs.length; ) {
            graphs[i] = new FastUtilGraph();
        }

        Executor executor = Executors.newCachedThreadPool();

        // Construct the Disruptor
        WaitStrategy waitStrategy = new YieldingWaitStrategy(); //55k, 70% cpu
        //WaitStrategy waitStrategy = new SleepingWaitStrategy(5); // 50k, 40% cpu, slow test on empty
        //WaitStrategy waitStrategy = new TimeoutBlockingWaitStrategy(1, TimeUnit.SECONDS); // 37k, zero cpu
        //WaitStrategy waitStrategy = new BlockingWaitStrategy(); //35k, zero cpu
        //WaitStrategy waitStrategy = new BusySpinWaitStrategy();   //30k, high cpu, errors
        //WaitStrategy waitStrategy = new LiteBlockingWaitStrategy(); //37k, zero cpu
        //WaitStrategy waitStrategy = new LiteTimeoutBlockingWaitStrategy(1, TimeUnit.SECONDS); // 34k, zero cpu
        //WaitStrategy waitStrategy = new PhasedBackoffWaitStrategy(1, 1, TimeUnit.SECONDS, new SleepingWaitStrategy(5)); // 30k, half cpu, errors
        //WaitStrategy waitStrategy = new SleepingWaitStrategy(); // 50k, 40% cpu

        Disruptor<ExchangeEvent> disruptor = new Disruptor<>(ExchangeEvent::new, conf.getInt("uranus.disruptor.ring_buffer_size"), executor,
                ProducerType.SINGLE, waitStrategy);

        DatabaseEventHandler[] handlers = new DatabaseEventHandler[THREADS];
        for (int i = -1; ++i < THREADS; ) {
            handlers[i] = new DatabaseEventHandler(i);
        }

        // Connect the handlers
        disruptor.handleEventsWith(new PersistenceHandler())
                .then(handlers);

        // Start the Disruptor, get the ring buffer from the Disruptor to be used for publishing.
        ringBuffer = disruptor.start();
    }

    public static void main(final String[] args) throws ServletException {
        Config conf = ConfigFactory.load("uranus");
        UranusServer uranusServer = new UranusServer(conf);
        uranusServer.buildAndStartServer(conf);
    }

    public void buildAndStartServer() throws ServletException {
        Config conf = ConfigFactory.load("uranus");
        buildAndStartServer(conf);
    }

    public void buildAndStartServer(Config conf) throws ServletException {

        undertow = Undertow.builder()
                .addHttpListener(conf.getInt("uranus.server.port"), conf.getString("uranus.server.host"))
                .setBufferSize(conf.getInt("uranus.server.buffer_size"))
                .setWorkerThreads(THREADS)
                .setIoThreads(1)
                .setHandler(Handlers.path()
                    .addPrefixPath("/db", Handlers.routing()
                        .add(GET, "/test", e -> e.setStatusCode(StatusCodes.OK))
                        .add(GET, "/noop", new RequestHandler(false, Action.NOOP))

                        .add(GET, PATH_REL_TYPES, new RequestHandler(false, Action.GET_RELATIONSHIP_TYPES))
                        .add(GET, PATH_REL_TYPES_COUNT, new RequestHandler(false, Action.GET_RELATIONSHIP_TYPES_COUNT))
                        .add(GET, PATH_REL_TYPE_COUNT, new RequestHandler(false, Action.GET_RELATIONSHIP_TYPE_COUNT))

                        .add(GET, PATH_NODE, new RequestHandler(false, Action.GET_NODE))
                        .add(POST, PATH_NODE, new RequestHandler(true, Action.POST_NODE))
                        .add(DELETE, PATH_NODE, new RequestHandler(true, Action.DELETE_NODE))
                        .add(PUT, PATH_NODE_PROPERTIES, new RequestHandler(true, Action.PUT_NODE_PROPERTIES))
                        .add(DELETE, PATH_NODE_PROPERTIES, new RequestHandler(true, Action.DELETE_NODE_PROPERTIES))
                        .add(GET, PATH_NODE_PROPERTY, new RequestHandler(false, Action.GET_NODE_PROPERTY))
                        .add(PUT, PATH_NODE_PROPERTY, new RequestHandler(true, Action.PUT_NODE_PROPERTY))
                        .add(DELETE, PATH_NODE_PROPERTY, new RequestHandler(true, Action.DELETE_NODE_PROPERTY))

                        .add(GET, PATH_REL, new RequestHandler(false, Action.GET_RELATIONSHIP))
                        .add(POST, PATH_REL, new RequestHandler(true, Action.POST_RELATIONSHIP))
                        .add(DELETE, PATH_REL, new RequestHandler(true, Action.DELETE_RELATIONSHIP))
                        .add(PUT, PATH_REL_PROPERTIES, new RequestHandler(true, Action.PUT_RELATIONSHIP_PROPERTIES))
                        .add(DELETE, PATH_REL_PROPERTIES, new RequestHandler(true, Action.DELETE_RELATIONSHIP_PROPERTIES))
                        .add(GET, PATH_REL_PROPERTY, new RequestHandler(false, Action.GET_RELATIONSHIP_PROPERTY))
                        .add(PUT, PATH_REL_PROPERTY, new RequestHandler(true, Action.PUT_RELATIONSHIP_PROPERTY))
                        .add(DELETE, PATH_REL_PROPERTY, new RequestHandler(true, Action.DELETE_RELATIONSHIP_PROPERTY))
                        // Additional Relationships
                        .add(GET, PATH_ADD_REL, new RequestHandler(false, Action.GET_RELATIONSHIP))
                        .add(DELETE, PATH_ADD_REL, new RequestHandler(true, Action.DELETE_RELATIONSHIP))
                        .add(PUT, PATH_ADD_REL_PROPERTIES, new RequestHandler(true, Action.PUT_RELATIONSHIP_PROPERTIES))
                        .add(DELETE, PATH_ADD_REL_PROPERTIES, new RequestHandler(true, Action.DELETE_RELATIONSHIP_PROPERTIES))
                        .add(GET, PATH_ADD_REL_PROPERTY, new RequestHandler(false, Action.GET_RELATIONSHIP_PROPERTY))
                        .add(PUT, PATH_ADD_REL_PROPERTY, new RequestHandler(true, Action.PUT_RELATIONSHIP_PROPERTY))
                        .add(DELETE, PATH_ADD_REL_PROPERTY, new RequestHandler(true, Action.DELETE_RELATIONSHIP_PROPERTY))

                        .add(GET, PATH_RELATED, new RequestHandler(false, Action.GET_RELATED))
                        .add(GET, PATH_RELATED_TYPE, new RequestHandler(false, Action.GET_RELATED_TYPE)))

                    .addPrefixPath("/swagger", createStaticSwaggerUIHandler()))
                .build();

        undertow.start();
    }

    public void stopServer() {
        if (undertow != null) {
            undertow.stop();
        }
    }

    private HttpHandler createStaticSwaggerUIHandler() {
        final ResourceManager staticResources =
                new ClassPathResourceManager(getClass().getClassLoader(), "static/swagger-ui");
        final ResourceHandler resourceHandler = new ResourceHandler(staticResources);
        resourceHandler.setDirectoryListingEnabled(false);
        resourceHandler.setWelcomeFiles("index.html");
        return resourceHandler;
    }

}
