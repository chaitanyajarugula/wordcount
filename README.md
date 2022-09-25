# Word Count Assignment - 1
## Teammates: Chaitanya Jarugula, Tarun Sai Naregudem, Karthikeya Reddy Kuchuru
### General Information:

* Operating System:         Mac -> Microsoft Remote Desktop, Windows -> Default Remote Desktop, Ubuntu -> Remmina
* Machine:                  cs6304-<mst_username>-01.class.mst.edu
* User:                     <mst_username>
* Default Password:         <mst_password>

### Importing Project:
* Open “eclipse”, right click on “Package Explorer” window, click import.
* Select “Git”-> “Projects from Git” and click “next”.
* Select “clone url” and click “next”.
* Paste “ https://github.com/chaitanyajarugula/wordcount ” in the “url” textbox, and click “next”. 
* Choose “Import existing project” and click “finish”.

### Referencing libraries:
* Right click on project and select “build path”-> “configure build path” ->”libraries”->”add external jars”.
* Go to "home" -> "git" -> "wordcount" -> "lib" and select all jars and click ok.

### Input file:
* Open folder "file", you will see the input file named "WordCount.txt"

### Output jar:
* Right click on project and select "Export".
* Choose type "Java" -> "Jar file" and click "next".
* Select the export destination as "home" -> "git" -> "WordCount" -> "jar" and click "Finish".

### Hadoop Commands:
```
hadoop fs -mkdir InputFolder                                      //to create a new input folder
hadoop fs -copyFromLocal <input file> InputFolder                  //to copy a file from local directory to hadoop environment
hadoop fs -ls InputFolder                                          //to see the files inside "InputFolder"
hadoop jar <jar file name> <class name> InputFolder OutputFolder   //running mapreduce operation
hadoop fs -ls OutputFolder                                        //to see the files inside "OutputFolder"
hadoop fs -cat OutputFolder/part-r-00000                          //to see the content inside "OutputFolder/part-r-00000" file
hadoop fs -rm -r OutputFolder                                     //to remove "OutputFolder" directory and all its files
```

- remove OutputFolder before generating the next results.
- remove/clean InputFolder if you want to use a different file as input.

### Common Errors:
Error 1: mkdir: Call From cs6304-cj9p5-02/127.0.1.1 to localhost:9000 failed on connection exception: java.net.ConnectException: Connection refused  
Explanation and Fix: In general this error comes if you are running hadoop first time on your VM after a reset. The below commands will fix it.
```
stop-all.sh
hadoop namenode -format
start-all.sh
```
You can use the below command to check if namenode, datanode and nodemanager are running.
```
jps

```

Error 2: mkdir: `hdfs://localhost:9000/user/<username>': No such file or directory  
Explanation and Fix: The error comes when there is no directory /user and /user/<username> in hdfs and you are trying to create a folder using "hadoop fs -mkdir InputFolder ".   
Below command will create the directory structure if required and solves the problem.
```
hdfs dfs -mkdir -p InputFolder
```

Warning 1: WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable  
Fix: You can just ignore this warning.

### Summary and Contributions:
1. **Get Rid of Punctuations (Team work):** We used regex pattern to filter all the letters that does not fall under the specified category and replaced them with spaces
2. **Get rid of stop-words(Team Work):** We generated a astopwords file which included all the majority stop words such as a,as,the etc., and we made a hashset of string datatype and appended all the data in the file to the hashset which is further used to filter the words in mapper.
3. **Individual Part-1 (Tarun Sai Naregudem):** I have converted the line to string and used .toLowerCase() to convert all words into lower case which enabled case-insensitivity in the document. 
4. **Indivudal Part-2 (karthikeya Kuchuru Reddy):** I have extended Individual part-1 with a cleanup function where I have used an array list of entries with key as text and value as IntWritable datatype and used collections.sort and implemented the abstract method compare where two values of different entries are compared with each other.
5. **Individual Part-3 (Chaitanya Jarugula):** I have modfied the key and value class of mapper and reducer. Mapper consists of a {text,text} classes where key is the first letter of the word and value is the combination of one_<length of the word>. reducer interprets and splits the values into total count and sum of length of the words. reducer finally writes word, average calculated.
