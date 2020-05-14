package src.main.java;

import javafx.util.Pair;
import org.zeromq.*;

import java.util.ArrayList;
import java.util.List;

public class Proxy {
    private static final List<Info> storeList = new ArrayList<>();
    private static ZMQ.Socket frontend;
    private static ZMQ.Socket backend;

    private static void updateHeartBeat(String id) {
        for (Info info : storeList) {
            if (info.getId().equals(id)) {
                info.setHeartBeat(System.currentTimeMillis());
            }
        }
    }

    private static void removeDeadStore() {
        storeList.removeIf(Info::isDead);
    }

    private static boolean sendGetReq(Integer key, ZMsg msg) {
        for (Info info : storeList) {
            if (info.getStart() <= key && key <= info.getEnd()) {
                info.getAddress().send(backend, ZFrame.REUSE + ZFrame.MORE);
                msg.send(backend, false);
                return true;
            }
        }
        return false;
    }

    private static boolean sendSetReq(Integer key, ZMsg msg) {
        boolean isKeyValid = false;

        for (Info info : storeList) {
            if (info.getStart() <= key && key <= info.getEnd()) {
                info.getAddress().send(backend, ZFrame.REUSE + ZFrame.MORE);
                msg.send(backend, false);
                isKeyValid = true;
            }
        }
        return isKeyValid;
    }

    public static void main(String[] args) {
        ZContext context = new ZContext();
        frontend = context.createSocket(SocketType.ROUTER);
        backend = context.createSocket(SocketType.ROUTER);

        frontend.bind(Client.CLIENT_ADDRESS);
        backend.bind(Storage.STORAGE_ADDRESS);

        ZMQ.Poller items = context.createPoller(2);
        items.register(frontend, ZMQ.Poller.POLLIN);
        items.register(backend, ZMQ.Poller.POLLIN);


        while (!Thread.currentThread().isInterrupted()) {
            items.poll(Storage.TIMEOUT);

            if (items.pollin(0)) {
                ZMsg msg = ZMsg.recvMsg(frontend);
                String com = new String(msg.getLast().getData(), ZMQ.CHARSET);
                Commands.CommandType type = Commands.getCommandType(com);

                if (type == Commands.CommandType.GET) {
                    Integer key = Commands.getKey(com);
                    Boolean isKeyValid = sendGetReq(key, msg);

                    if (!isKeyValid) {
                        msg.getLast().reset(Commands.setResponseCommand("Out of array"));
                        msg.send(frontend);
                    }
                }

                if (type == Commands.CommandType.SET) {
                    Integer key = Commands.getKey(com);
                    Boolean isKeyValid = sendGetReq(key, msg);

                    String response = isKeyValid ?
                            Commands.setResponseCommand("done") : Commands.setResponseCommand("Out of array");

                    ZMsg responseMessage = new ZMsg();
                    responseMessage.add(new ZFrame(response));
                    responseMessage.wrap(msg.getFirst());
                    responseMessage.send(frontend);
                }
            }

            if (items.pollin(1)) {
                ZMsg msg = ZMsg.recvMsg(backend);
                ZFrame address = msg.unwrap();
                String id = new String(address.getData(), ZMQ.CHARSET);

                String com = new String(msg.getLast().getData(), ZMQ.CHARSET);
                Commands.CommandType type = Commands.getCommandType(com);

                if (type == Commands.CommandType.CONNECT) {
                    Pair<Integer, Integer> range = Commands.getKeyValue(com);

                    storeList.add(new Info(
                            id, address, range.getKey(), range.getValue(), System.currentTimeMillis()
                    ));

                } else if (type == Commands.CommandType.NOTIFY) {
                    updateHeartBeat(id);
                } else if (type == Commands.CommandType.RESPONSE) {
                    msg.send(frontend);
                }
            }

            removeDeadStore();
        }

        context.destroySocket(frontend);
        context.destroySocket(backend);
        context.destroy();
    }
}
