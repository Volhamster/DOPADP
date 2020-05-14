package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Result  implements Serializable{
    private final String result;
    private final String expectedResult;
    private final Boolean success;

    @JsonCreator
    public Result(
            @JsonProperty("result") String result,
            @JsonProperty("expectedResult") String expectedResult,
            @JsonProperty("success") Boolean success
    ) {
        this.result = result;
        this.expectedResult = expectedResult;
        this.success = success;
    }

    public String getResult() {
        return result;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public Boolean getSuccess() {
        return success;
    }

}

