package main.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class TestPackage implements Serializable{
    private final String packageId;
    private final String jsScript;
    private final String functionName;
    private final List<Test> tests;

    @JsonCreator
    public TestPackage(
            @JsonProperty("packageId") String packageId,
            @JsonProperty("jsScript") String jsScript,
            @JsonProperty("functionName") String functionName,
            @JsonProperty("tests") List<Test> tests
    ) {
        this.packageId = packageId;
        this.jsScript = jsScript;
        this.functionName = functionName;
        this.tests = tests;
    }

    public String getPackageId() {
        return packageId;
    }

    public String getJsScript() {
        return jsScript;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<Test> getTests() {
        return tests;
    }
}