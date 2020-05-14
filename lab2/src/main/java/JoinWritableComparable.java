package src.main.java;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;

import java.io.IOException;

public class JoinWritableComparable implements WritableComparable<JoinWritableComparable> {
    private int airport_id;
    private int indicator;

    public JoinWritableComparable() {
        this.airport_id = 0;
    }

    public JoinWritableComparable(int airport_id, int indicator) {
        this.airport_id = airport_id;
        this.indicator = indicator;
    }

    public void setAirport_id(int airport_id) {
        this.airport_id = airport_id;
    }

    public int getAirport_id() {
        return airport_id;
    }

    public void setIndicator(int indicator) {
        this.indicator = indicator;
    }

    public int getIndicator() {
        return indicator;
    }

    @Override
    public void readFields(DataInput input) throws IOException {
        airport_id = input.readInt();
        indicator = input.readInt();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(airport_id);
        output.writeInt(indicator);
    }

    @Override
    public int compareTo(JoinWritableComparable a) {
        if (this.airport_id > a.airport_id) {
            return 1;
        } else if (this.airport_id != a.airport_id) {
            return -1;
        }
        if (this.indicator > a.indicator) {
            return 1;
        } else if (this.indicator != a.indicator) {
            return -1;
        }
        return 0;
    }

    public int compareToAirport_id(JoinWritableComparable a) {
        if (this.airport_id > a.airport_id) {
            return 1;
        } else if (this.airport_id != a.airport_id) {
            return -1;
        }
        return 0;
    }
}