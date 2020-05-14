package src.main.java;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class JoinComparator extends WritableComparator {
    public JoinComparator() {
        super(JoinWritableComparable.class, true);
    }

    @Override
    public int compare(WritableComparable obj1, WritableComparable obj2) {
        JoinWritableComparable a = (JoinWritableComparable) obj1;
        JoinWritableComparable b = (JoinWritableComparable) obj2;
        return a.compareToAirport_id(b);
    }
}