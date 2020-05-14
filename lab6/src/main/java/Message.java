package src.main.java;

public class Message {
    private String[] serversList;


    public Message(String[] serversList) {
        this.serversList = serversList;
    }

    public String[] getServersList() {
        return serversList;
    }

    public void setServersList(String[] serversList) {
        this.serversList = serversList;
    }
}
