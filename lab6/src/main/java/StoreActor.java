package src.main.java;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.Random;

public class StoreActor extends AbstractActor {

    private String[] serversList;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.class, msg -> {
                    this.serversList = msg.getServersList();
                })
                .match(RandomServerMessage.class, msg -> sender().tell(getRandomServer(), self()))
                .build();
    }

    private String getRandomServer() {
        return serversList[new Random().nextInt(serversList.length)];
    }

    static Props props() {
        return Props.create(StoreActor.class);
    }
}