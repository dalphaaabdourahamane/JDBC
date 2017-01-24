package R0;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;

/**
 * Created by dialal14 on 31/10/16.
 */
public class requestR0 {
    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text,Text,IntWritable>{

        @Override
        public void map(LongWritable longWritable, Text text, OutputCollector<Text, IntWritable> outputCollector, Reporter reporter) throws IOException {
            String[] split = text.toString().split(",");
            int total = Integer.parseInt(split[1]);
            if(total > 1000){
                outputCollector.collect(new Text(split[0]),new IntWritable(1));
            }
        }
    }
    public static void main(String[] args) throws IOException {
        JobConf jobConf = new JobConf(requestR0.class);
        jobConf.setJobName("requeste R0");

        jobConf.setOutputKeyClass(Text.class);
        jobConf.setOutputValueClass(IntWritable.class);

        jobConf.setMapperClass(Map.class);

        jobConf.setInputFormat(TextInputFormat.class);
        jobConf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(jobConf, new Path(args[0]));
        FileOutputFormat.setOutputPath(jobConf, new Path(args[1]));
        JobClient.runJob(jobConf);
    }
}
