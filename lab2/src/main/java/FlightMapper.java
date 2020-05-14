package src.main.java;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlightMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final int AIRPORT_ID = 14;
    private static final int DELAY_TIME = 18;

    private static final float ZERO = 0.0f;

    private static final String EMPTY_STRING = "";

    @Override
    protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {

        String[] words = value.toString().split(",");

        if (key.get() > 0) {
            float delay = ZERO;
            if (!words[DELAY_TIME].equals(EMPTY_STRING))
                delay = Float.parseFloat(words[DELAY_TIME]);

            if (delay > ZERO) {
                int airport_id = Integer.parseInt(words[AIRPORT_ID]);
                context.write(
                        new JoinWritableComparable(airport_id, 1),
                        new Text(words[DELAY_TIME])
                );
            }
        }
    }
}
