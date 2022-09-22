import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

  public static class MyMapper 
       extends Mapper<Object, Text, Text, IntWritable>{
    
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
    //private int lineCount = 1;
    HashSet <String> stopwords = new HashSet<>();
    public void setup(Context context) throws IOException {
    	File file = new File("/home/cj9p5/assignment_1/stopwords.txt");
    	@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(file));
    	String str;
    	while( (str= br.readLine())!= null) {
    		stopwords.add(str);
    	}
    	System.out.println("words log" + stopwords);
    }
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
    	String line =value.toString().toLowerCase();
    	line = line.replaceAll("[^a-zA-Z0-9 ]", " ");
      StringTokenizer itr = new StringTokenizer(line);
      while (itr.hasMoreTokens()) {
    	  String token1 = itr.nextToken();
    	  word.set(token1);
        if(stopwords.contains(token1))
        	continue;
        
        context.write(word, one);
      }
      //lineCount++;
    }
  }
  
  public static class MyReducer 
       extends Reducer<Text,IntWritable,Text,IntWritable> {
	  
    private IntWritable result = new IntWritable();
//    private Map<Text, IntWritable> countMap = new HashMap<Text, IntWritable>();

    public void reduce(Text key, Iterable<IntWritable> values, 
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
      
    	 
     }
//    public void cleanup(Context context) {
//    	//Method used to sort data
//    	
//    }
//      countMap.put(new Text(key), new IntWritable(sum));
    
//    Map<Text,IntWritable> countMap = new HashMap<Text,IntWritable>();
//    public void cleanup(Context context) throws IOException, InterruptedException {
//    	List<Entry<Text, IntWritable>> countList = new ArrayList<Entry<Text, IntWritable>>(countMap.entrySet());
//    	Collections.sort( countList, new Comparator<Entry<Text, IntWritable>>(){
//            public int compare( Entry<Text, IntWritable> o1, Entry<Text, IntWritable> o2 ) {
//                return (o2.getValue()).compareTo( o1.getValue() );
//            }
//        } );
//    	for(Entry<Text, IntWritable> entry: countList) {    	
//	    	context.write(entry.getKey(), entry.getValue());
//	    }	
//    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    
    Job job = new Job(conf, "word count");
    job.setJarByClass(WordCount.class);
    job.setMapperClass(MyMapper.class);
    job.setReducerClass(MyReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
