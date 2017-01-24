package R0;

import com.sun.org.apache.regexp.internal.RE;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by dialal14 on 31/10/16.
 */
public class requestR1 {
    public static class Map extends MapReduceBase
            implements Mapper<LongWritable, Text,IntWritable,IntWritable>{


        @Override
        public void map(LongWritable longWritable, Text text, OutputCollector<IntWritable, IntWritable> outputCollector, Reporter reporter) throws IOException {
            String[] split = text.toString().split(",");
            int total = Integer.parseInt(split[1]);
            if(total > 1000){
                outputCollector.collect(new IntWritable(Integer.parseInt(split[0])),new IntWritable());
            }
        }

    }

    public static class Reduce extends MapReduceBase implements Reducer<IntWritable,IntWritable,IntWritable,IntWritable>{

        @Override
        public void reduce(IntWritable intWritable, Iterator<IntWritable> iterator, OutputCollector<IntWritable, IntWritable> outputCollector, Reporter reporter) throws IOException {
            outputCollector.collect(intWritable,null);
        }
    }


    public static void main(String[] args) throws IOException {
        JobConf jobConf = new JobConf(requestR1.class);
        jobConf.setJobName("requeste R1");

        jobConf.setOutputKeyClass(IntWritable.class);
        jobConf.setOutputValueClass(IntWritable.class);

        jobConf.setMapperClass(Map.class);
        jobConf.setCombinerClass(Reduce.class);
        jobConf.setReducerClass(Reduce.class);

        jobConf.setInputFormat(TextInputFormat.class);
        jobConf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(jobConf, new Path(args[0]));
        FileOutputFormat.setOutputPath(jobConf, new Path(args[1]));
        JobClient.runJob(jobConf);
    }
}
