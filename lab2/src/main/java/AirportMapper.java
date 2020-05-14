package src.main.java;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class AirportMapper extends Mapper<LongWritable, Text, JoinWritableComparable, Text> {

    private static final int AIRPORT_ID = 0;
    private static final int AIRPORT_NAME = 1;

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] words = value.toString().split(",[\"]");

        if (key.get() > 0) {
            int airport_id = Integer.parseInt(words[AIRPORT_ID].replaceAll("\"", ""));
            context.write(
                    new JoinWritableComparable(airport_id, 0),
                    new Text(words[AIRPORT_NAME].replaceAll("\"", ""))
            );
        }
    }
}
