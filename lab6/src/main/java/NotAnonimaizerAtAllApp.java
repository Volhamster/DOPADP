package src.main.java;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class NotAnonimaizerAtAllApp extends AllDirectives {

    private static final String HOST = "localhost";
    private static final int port = 8080;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        //int port = Integer.parseInt(args[0]);

        System.out.println("start!");
        ActorSystem system = ActorSystem.create("routes");
        ActorRef storeActor = system.actorOf(StoreActor.props(), "storeActor");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        Server server = new Server(http, port, storeActor);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = server.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(HOST, port),
                materializer
        );

        System.out.println("Server online");
        System.in.read();

        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate()); // and shutdown when done
    }
}