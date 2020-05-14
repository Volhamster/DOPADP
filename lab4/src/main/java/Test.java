package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Test implements Serializable{
    private final String testName;
    private String result;
    private final String expectedResult;
    private final Object[] params;
    private TestPackage testPackage;

    @JsonCreator
    public Test(
            @JsonProperty("testName") String testName,
            @JsonProperty("expectedResult") String expectedResult,
            @JsonProperty("params") Object[] params
    ) {
        this.testName = testName;
        this.expectedResult = expectedResult;
        this.params = params;
    }

    public String getTestName() {
        return testName;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public Object[] getParams() {
        return params;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result=result;
    }

    public TestPackage getTestPackage() {
        return testPackage;
    }

    public void setTestPackage(TestPackage testPackage){
        this.testPackage = testPackage;
    }

}
