package main.java;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

import java.util.*;

public class StoreActor extends AbstractActor {

    private Map<String, ArrayList<Test>> storage = new HashMap<>();

    private void addTest(Test test){
        String packageId = test.getTestPackage().getPackageId();
        if (this.storage.containsKey(packageId)){
            this.storage.get(packageId).add(test);
        } else {
            ArrayList<Test> tests = new ArrayList<>();
            tests.add(test);
            this.storage.put(packageId, tests);
        }
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(Test.class, t -> this.addTest(t))
                .match(String.class, p -> sender().tell())
                .build();
    }

    static Props props() {
        return Props.create(StoreActor.class);
    }
}
