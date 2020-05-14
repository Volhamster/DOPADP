package main.java;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.PathMatchers.segment;

public class TestApp extends AllDirectives {

    private static final String HOST = "localhost";
    private static final int PORT = 9002;
    private static final int TIME_OUT_MILLIS = 5000;

    private ActorRef routeActor;

    private TestApp(ActorRef routeActor) {
        this.routeActor = routeActor;
    }

    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("test");
        ActorRef routeActor = system.actorOf(Props.create(RouterActor.class, system));

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        TestApp app = new TestApp(routeActor);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute(system).flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(HOST, PORT),
                materializer
        );

        System.out.println("Server online");
        System.in.read();

        binding.thenCompose(ServerBinding::unbind).thenAccept(unbound -> system.terminate());
    }

    private Route createRoute(ActorSystem system) {
        return concat(
                get(() -> pathPrefix("getPackage", () ->
                                path(segment(), (String id) -> {
                                    scala.concurrent.Future<Object> res = Patterns.ask(
                                            routeActor,
                                            id,
                                            TIME_OUT_MILLIS
                                    );
                                    return completeOKWithFuture(res, Jackson.marshaller());
                                })
                        )
                ),
                post(() -> path("postPackage", () ->
                                entity(Jackson.unmarshaller(Package.class),
                                        msg -> {
                                            routeActor.tell(msg, ActorRef.noSender());
                                            return complete("Test started \n");
                                        })
                        )
                )
        );
    }
}