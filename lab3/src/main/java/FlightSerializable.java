package src.main.java;

import java.io.Serializable;

public class FlightSerializable implements Serializable {

    private int originAirportId;
    private int destAirportId;
    private float delayTime;
    private int isCancelled;

    public FlightSerializable() {
    }

    public FlightSerializable(int originAirportId, int destAirportId, float delayTime, int isCancelled) {
        this.originAirportId = originAirportId;
        this.destAirportId = destAirportId;
        this.delayTime = delayTime;
        this.isCancelled = isCancelled;
    }

    public void setOriginAirportId(int originAirportId) {
        this.originAirportId = originAirportId;
    }

    public void setDestAirportId(int destAirportId) {
        this.destAirportId = destAirportId;
    }

    public void setDelayTime(float delayTime) {
        this.delayTime = delayTime;
    }

    public void setIsCancelled(int isCancelled) {
        this.isCancelled = isCancelled;
    }

    public float getOriginAirportId() {
        return originAirportId;
    }

    public int getDestAirportId() {
        return destAirportId;
    }

    public float getDelayTime() {
        return delayTime;
    }

    public int getIsCanceled() {
        return isCancelled;
    }
}