package src.main.java;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.http.javadsl.model.Uri;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.pattern.Patterns;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class Server extends AllDirectives {

    private static final int SEC = 5;

    private Http http;
    private ActorRef storeActor;
    private Duration duration = Duration.ofSeconds(SEC);

    private static final String URL = "url";
    private static final String COUNT = "count";

    private void zooKeeperInitialization(int port) throws IOException, KeeperException, InterruptedException {
        Zoo zoo = new Zoo(storeActor);
        zoo.createServer(getServerURL(port));
    }

    public Server(final Http http, int port, ActorRef storeActor) throws IOException, KeeperException, InterruptedException {
        this.http = http;
        this.storeActor = storeActor;
        zooKeeperInitialization(port);
    }

    private String getServerURL(int port) {
        return "http://localhost:" + port;
    }

    private CompletionStage<HttpResponse> fetch(String url) {
        return http.singleRequest(HttpRequest.create(url));
    }

    private CompletionStage<HttpResponse> redirect(String url, int count) {
        return Patterns.ask(storeActor, new RandomServerMessage(), duration)
                .thenCompose(serverURL -> fetch(createUrl((String) serverURL, url, count)));
    }

    private String createUrl(String serverURL, String queryURL, int count) {
        return Uri.create(serverURL)
                .query(Query.create(
                        Pair.create(URL, queryURL),
                        Pair.create(COUNT, Integer.toString(count - 1))
                        )
                )
                .toString();
    }

    public Route createRoute() {
        return get(() ->
                parameter(URL, url ->
                        parameter(COUNT, count -> {
                                    int k = Integer.parseInt(count);
                                    if (k == 0) {
                                        return completeWithFuture(fetch(url));
                                    } else {
                                        return completeWithFuture(redirect(url, k));
                                    }
                                }
                        )
                )
        );
    }
}
