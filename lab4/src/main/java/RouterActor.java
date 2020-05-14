package main.java;

import akka.actor.*;
import akka.routing.RoundRobinPool;
import scala.concurrent.duration.Duration;

import java.util.Collections;

public class RouterActor extends AbstractActor {

    private final int MAX_RETRIES = 10;
    private final int MAX_WORK = 10;


    private ActorRef storeActor;
    private ActorRef testActor;
    private SupervisorStrategy strategy;

    public RouterActor(ActorSystem sys) {
        this.storeActor = sys.actorOf(StoreActor.props(), "Store");
        this.strategy = new OneForOneStrategy(
                MAX_RETRIES,
                Duration.create("1 minute"),
                Collections.singletonList(Exception.class)
        );
        this.testActor = sys.actorOf(
                new RoundRobinPool(MAX_WORK)
                        .withSupervisorStrategy(strategy)
                        .props(Props.create(TestActor.class, storeActor))
        );
    }

    private void runTests(TestPackage pkg) {
        for (Test test : pkg.getTests()) {
            test.setTestPackage(pkg);
            testActor.tell(test, ActorRef.noSender());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TestPackage.class, msg -> runTests(msg))
                .match(String.class, msg -> storeActor.forward(msg, getContext()))
                .build();
    }
}