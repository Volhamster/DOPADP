package src.main.java;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class JoinReducer extends Reducer<JoinWritableComparable, Text, Text, Text> {

    private static final float ZERO_F = 0.0f;
    private static final int ZERO_I = 0;

    @Override
    protected void reduce(JoinWritableComparable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

        Iterator<Text> iter = values.iterator();
        Text airport_name = new Text("Airport name: " + iter.next().toString());

        if (iter.hasNext()) {
            float min = Float.MAX_VALUE;
            float max = ZERO_F;
            float average = ZERO_F;
            int count = ZERO_I;
            float a;

            while (iter.hasNext()) {
                a = Float.parseFloat(iter.next().toString());
                max = Math.max(max, a);
                min = Math.min(min, a);
                average += a;
                count++;
            }
            average /= count;

            context.write(airport_name,
                    new Text("Average: " + Float.toString(average) + "\t" +
                            "Min: " + Float.toString(min) + "\t" +
                            "Max: " + Float.toString(max)));
        }
    }
}
