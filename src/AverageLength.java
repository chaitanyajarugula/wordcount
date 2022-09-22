import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AverageLength {

  public static class MyMapper 
       extends Mapper<Object, Text, Text, Text>{
    
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
    //private int lineCount = 1;
    HashSet <String> stopwords = new HashSet<>();
    @Override
    public void setup(Context context) throws IOException {
    	File file = new File("/home/cj9p5/assignment_1/stopwords.txt");
    	@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(file));
    	String str;
    	while( (str= br.readLine())!= null) {
    		stopwords.add(str);
    	}
    }
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
    	String line =value.toString().toLowerCase();
    	line = line.replaceAll("[^a-zA-Z0-9 ]", " ");
      StringTokenizer itr = new StringTokenizer(line);
      while (itr.hasMoreTokens()) {
        
        //word.set(itr.nextToken()+"\t"+lineCount);
    	  String token1 = itr.nextToken();
    	  word.set(token1);
        if(stopwords.contains(token1))
        	continue;
        String wordStr = word.toString();
        String wordFirstChar = String.valueOf(wordStr.charAt(0));
        Text word_text = new Text(wordFirstChar);
        context.write(word_text, new Text(one + "_"+wordStr.length()));
      }
      //lineCount++;
    }
  }
  
  public static class MyReducer 
       extends Reducer<Text,Text,Text,DoubleWritable> {
	  
    private IntWritable result = new IntWritable();
//    private Map<Text, IntWritable> countMap = new HashMap<Text, IntWritable>();
//    private SortedMap<Integer, Text> tmap2;
    
//    @Override
//    public void setup(Context context) throws IOException,
//                                     InterruptedException
//    {
//        tmap2 = new ConcurrentSkipListMap<Integer, Text>();
//    }
    @Override
    public void reduce(Text key, Iterable<Text> values, 
                       Context context
                       ) throws IOException, InterruptedException {
      double sum = 0;
      double total_count = 0;
      final Iterator<Text> itr = values.iterator();
      while(itr.hasNext()) {
    	  String text = itr.next().toString();
    	  String [] tokens = text.split("_");
    	  final int length = Integer.parseInt(tokens[1]);
    	  final int count_vals = Integer.parseInt(tokens[0]);
    	  sum+=length;
    	  total_count+=count_vals;
      }
      final double average = sum/total_count;
     context.write(key, new DoubleWritable(average));
//    public void cleanup(Context context) {
//    	//Method used to sort data
//    	
//    }
//     countMap.put(new Text(key), new IntWritable(sum));
    }
    //private Map<Text,IntWritable> countMap = new HashMap<Text,IntWritable>();
//    @Override
//  public void cleanup(Context context) throws IOException, InterruptedException {
//	List<Entry<Text, IntWritable>> countList = new ArrayList<Entry<Text, IntWritable>>(countMap.entrySet());
//	int count = 0;
//	Collections.sort( countList, new Comparator<Entry<Text, IntWritable>>(){
//        public int compare( Entry<Text, IntWritable> o1, Entry<Text, IntWritable> o2 ) {
//            return (o2.getValue()).compareTo( o1.getValue() );
//        }
//    } );
//	for(Entry<Text, IntWritable> entry: countList) {  
//		count+=1;
//		context.write(entry.getKey(), entry.getValue());
//		if(count==200) break;
//    }	
//}
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    
    Job job = new Job(conf, "word count");
    job.setJarByClass(SortedWordCount.class);
    job.setMapperClass(MyMapper.class);
    job.setReducerClass(MyReducer.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(DoubleWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
