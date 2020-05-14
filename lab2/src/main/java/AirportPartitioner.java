package src.main.java;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class AirportPartitioner extends Partitioner<JoinWritableComparable, Text> {
    @Override
    public int getPartition(JoinWritableComparable key, Text value, int numReduceTasks) {
        return (key.getAirport_id() & Integer.MAX_VALUE) % numReduceTasks;
    }
}
