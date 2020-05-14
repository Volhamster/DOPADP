package src.main.java;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Scanner;

public class Client {
    public final static String CLIENT_ADDRESS = "tcp://localhost:8080";

    public static void main(String[] args) {
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.REQ);
        socket.connect(CLIENT_ADDRESS);

        System.out.println("START");
        Scanner in = new Scanner(System.in);

        while (true) {
            String command = in.nextLine();
            Commands.CommandType type = Commands.getCommandType(command);

            if (type == Commands.CommandType.EXIT) {
                break;
            }

            if (type == Commands.CommandType.INVALID) {
                System.out.println("No such command");
                continue;
            }

            socket.send(command, 0);
            String reply = socket.recvStr(0);
            System.out.println(reply);
        }
        context.destroySocket(socket);
        context.destroy();
    }
}
