package main.java;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TestActor extends AbstractActor {

    private ActorRef storeActor;

    TestActor(ActorRef storeActor) {
        this.storeActor = storeActor;
    }

    private static String testRun(StartTestMessage message) throws ScriptException, NoSuchMethodException {
        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval(message.getJsScript());
            Invocable invocable = (Invocable) engine;
            return invocable.invokeFunction(message.getFunctionName(),
                    //params
                    message.getTest().getParams()).toString();
        } catch (ScriptException err) {
            return "Error in " + err.getLocalizedMessage();
        } catch (NoSuchMethodException err) {
            return "Error in " + err.getLocalizedMessage();
        }
    }

//    private void StartTestReceive(StartTestMessage message) throws ScriptException, NoSuchMethodException {
//        getSender().tell(
//                new ResultMessage(
//                        message.getPackageId(),
//                        message.getTest(),
//                        testRun(message)),
//                ActorRef.noSender()
//        );
//    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartTestMessage.class,
                        msg -> storeActor.tell(testRun(msg), ActorRef.noSender()))
                .build();
    }

//    static Props props() {
//        return Props.create(TestActor.class);
//    }
}
