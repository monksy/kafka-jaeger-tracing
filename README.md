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



## TODO 

 * Write kafka data intializer 
 * Write topic creation script 
 * Word count processor 
 * Write Slow down Int to string 
 * Configure tracing

