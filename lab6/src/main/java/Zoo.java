package src.main.java;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Zoo {

    private static final String CONNECT_STRING = "127.0.0.1:2181";
    private static final Integer SESSION_TIMEOUT = 3000;
    private static final String PARENT_PATH = "/servers";
    private static final String CHILD_PATH = "/servers/s";


    private ZooKeeper zooKeeper;
    private ActorRef storeActor;

    public Zoo(ActorRef storeActor) throws IOException {
        this.zooKeeper = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, null);
        this.storeActor = storeActor;
        serverWatch();
    }

    public void createServer(String serverURL) throws KeeperException, InterruptedException {
        zooKeeper.create(
                CHILD_PATH,
                serverURL.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL
        );
    }


    private void serverWatch() {
        try {
            List<String> serverChildrenNames = zooKeeper.getChildren(PARENT_PATH,
                    watchedEvent -> {
                        if (watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged) serverWatch();
                    });

            List<String> serversNames = new ArrayList<>();

            for (String s : serverChildrenNames) {
                byte[] serverURL = zooKeeper.getData(PARENT_PATH + "/" + s, null, null);
                serversNames.add(new String(serverURL));
            }

            storeActor.tell(new Message(serversNames.toArray(new String[0])), ActorRef.noSender());
        } catch (KeeperException | InterruptedException err) {
            err.printStackTrace();
        }
    }
}
