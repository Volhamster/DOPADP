package src.main.java;

import javafx.util.Pair;

import java.util.regex.Pattern;

public class Commands {

    private static final Pattern SET_PATTERN = Pattern.compile("^SET \\d+ \\d+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern GET_PATTERN = Pattern.compile("^GET \\d+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EXIT_PATTERN = Pattern.compile("^F$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CONNECT_PATTERN = Pattern.compile("^CONNECT \\d+ \\d+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern NOTIFY_PATTERN = Pattern.compile("^NOTIFY$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RESPONSE_PATTERN = Pattern.compile("^RESPONSE: [a-z\\s\\d]+$", Pattern.CASE_INSENSITIVE);
    private static final String DELIMETR = " ";

    public enum CommandType {
        GET, SET, EXIT, CONNECT, NOTIFY, RESPONSE, INVALID
    }

    public static CommandType getCommandType(String com) {
        if (SET_PATTERN.matcher(com).find()) {
            return CommandType.SET;
        } else if (GET_PATTERN.matcher(com).find()) {
            return CommandType.GET;
        } else if (EXIT_PATTERN.matcher(com).find()) {
            return CommandType.EXIT;
        } else if (CONNECT_PATTERN.matcher(com).find()) {
            return CommandType.CONNECT;
        } else if (NOTIFY_PATTERN.matcher(com).find()) {
            return CommandType.NOTIFY;
        } else if (RESPONSE_PATTERN.matcher(com).find()) {
            return CommandType.RESPONSE;
        } else {
            return CommandType.INVALID;
        }
    }

    public static String setConnectCommand(int start, int end) {
        return "CONNECT " + start + DELIMETR + end;
    }

    public static String setResponseCommand(String resp) {
        return "RESPONSE: " + resp;
    }


    public static String setNotifyCommand() {
        return "NOTIFY";
    }

    public static String[] splitCommand(String com) {
        return com.split(DELIMETR);
    }

    public static Pair<Integer, Integer> getKeyValue(String com) {
        String[] comParts = splitCommand(com);
        return new Pair<>(Integer.parseInt(comParts[1]), Integer.parseInt(comParts[2]));
    }

    public static Integer getKey(String com) {
        String[] comParts = splitCommand(com);
        return Integer.parseInt(comParts[1]);
    }
}
