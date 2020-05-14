package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable{
    private final String packageId;
    private final List<Result> tests;

    @JsonCreator
    public Message(
            @JsonProperty("packageId") String packageId,
            @JsonProperty("tests") List<Result> tests
    ) {
        this.packageId = packageId;
        this.tests = tests;
    }
}

