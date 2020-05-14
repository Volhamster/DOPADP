package src.main.java;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class JoinApp {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: JoinApp <input path Airport> <input path Flight> <output path>");
            System.exit(-1);
        }

        Job job = Job.getInstance();
        job.setJarByClass(JoinApp.class);
        job.setJobName("Join App");

        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, FlightMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, AirportMapper.class);

        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        job.setPartitionerClass(AirportPartitioner.class);
        job.setGroupingComparatorClass(JoinComparator.class);
        job.setReducerClass(JoinReducer.class);

        job.setMapOutputKeyClass(JoinWritableComparable.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setNumReduceTasks(2);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
