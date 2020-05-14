package src.main.java;

//import zmq.ZMQ;

import javafx.util.Pair;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;
import src.main.java.Commands.*;

import java.util.HashMap;
import java.util.Map;

import static src.main.java.Commands.setConnectCommand;
import static src.main.java.Commands.setNotifyCommand;

public class Storage {
    public static final int TIMEOUT = 5000;
    public final static String STORAGE_ADDRESS = "tcp://localhost:9000";

    private static void sendConnectCommand(ZMQ.Socket socket, int start, int end) {
        socket.send(setConnectCommand(start, end), 0);
    }

    private static void sendNotifyCommand(ZMQ.Socket socket) {
        socket.send(setNotifyCommand(), 0);
    }

    public static void main(String[] args) {
        Map<Integer, Integer> storage = new HashMap<>();

        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.DEALER);
        socket.connect(STORAGE_ADDRESS);

        int start = Integer.parseInt(args[0]);
        int end = Integer.parseInt(args[1]);

        long heartBeat = System.currentTimeMillis() + TIMEOUT;
        sendConnectCommand(socket, start, end);

        while (!Thread.currentThread().isInterrupted()) {
            ZMsg msg = ZMsg.recvMsg(socket, false);

            if (msg != null) {
                String com = new String(msg.getLast().getData(), ZMQ.CHARSET);
                CommandType type = Commands.getCommandType(com);

                System.out.println(com);

                if (type == CommandType.GET) {
                    Integer key = Commands.getKey(com);
                    Integer value = storage.get(key);
                    String responseContent = value == null ? "null" : Integer.toString(value);

                    msg.getLast().reset(Commands.setResponseCommand(responseContent));
                    msg.send(socket);
                }

                if (type == CommandType.SET) {
                    Pair<Integer, Integer> setPar = Commands.getKeyValue(com);

                    storage.put(setPar.getKey(), setPar.getValue());
                    msg.destroy();
                }
            }

            if (System.currentTimeMillis() >= heartBeat) {
                heartBeat = System.currentTimeMillis() + TIMEOUT;
                sendNotifyCommand(socket);
            }
        }
        context.destroySocket(socket);
        context.destroy();
    }
}
