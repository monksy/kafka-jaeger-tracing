Project to demonstrate

## Projects

### Word Count 

The word count project takes in values coming in from a kafka topic and returns the total amount of times each 
#### Topic Names

 * **Input** wordcount-input
 * **Output** wordcount-output
#### Input 

This accepts anything for the key, and a string of text for the value. Each word will be counted. 

#### Output 

This outputs the word and the current count. This can be viewed as a KTable with word (string/key) and times it's been seen (int)

#### Build 

```sbt
sbt build assembly
```

#### Run 

```sbt
sbt run 
```



## How to run everything 

 * Start up kafka, zookeeper, jaeger with `./start.sh`
 * Open up a few console windows to create the topics
   * For the wordcount application 
   ```shell script
   kafka-console-consumer.sh \
     --formatter kafka.tools.DefaultMessageFormatter \
     --bootstrap-server localhost:9092 \
     --from-beginning \
     --topic wordcount-output \
     --property print.key=true \
     --property print.value=true \
     --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer  \
     --property key.separator="------"  
       
   ```
   * For the wordcount processor application 
   ```shell script
   kafka-console-consumer.sh \
        --formatter kafka.tools.DefaultMessageFormatter \
        --bootstrap-server localhost:9092 \
        --from-beginning \
        --topic wordcountprocessor-output \ 
        --property print.key=true \
        --property print.value=true \
        --property key.separator='------'   
   ```
 * Run both of the applications (word count and word count processor)
 * Open up your browser for the Jaeger UI: http://localhost:16686/search

   
## Helpful Resources



## TODO 
 * Configure tracing

