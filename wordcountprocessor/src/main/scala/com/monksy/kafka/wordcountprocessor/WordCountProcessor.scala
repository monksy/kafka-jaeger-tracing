package com.monksy.kafka.wordcountprocessor

import java.util.Properties
import java.util.concurrent.TimeUnit

import io.jaegertracing.Configuration
import io.opentracing.contrib.kafka.streams.TracingKafkaClientSupplier
import io.opentracing.util.GlobalTracer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}

/**
  * A kafka streams application that reads records words from an input topic and counts the occurrence of each word
  * and outputs this count to a different topic
  *
  * Before running this application,
  * start your kafka cluster and create the required topics
  *
  * kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic input-topic
  * kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic streams-wordcountprocessor-output
  *
  */
object WordCountProcessor extends App {

  val config = new Properties()
  // setting offset reset to earliest so that we can re-run the app with same data
  config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, StreamSettings.autoResetConfig)
  config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, StreamSettings.bootstrapServers)
  config.put(StreamsConfig.APPLICATION_ID_CONFIG, StreamSettings.appID)
  // max cache buffering set to 0
  // preferable during development, update value for production use
  config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0L: java.lang.Long)

  def topology(): StreamsBuilder = {
    val builder: StreamsBuilder = new StreamsBuilder
    val textLines =
      builder.stream[String, Integer](StreamSettings.inputTopic)

    val processed = textLines
        .map((k,v) => {
          //Heres the performance issue, lets see if open trace can find it.
          Thread.sleep(v * 1000)
          (k, s"We waited $v seconds for the word $k")
        })

    processed.print(Printed.toSysOut[String, String])
    processed.to(StreamSettings.outputTopic)

    builder
  }

  import io.jaegertracing.Configuration.{ReporterConfiguration, SamplerConfiguration}
  import org.apache.kafka.streams.KafkaStreams

  val samplerConfig = SamplerConfiguration.fromEnv.withType("const").withParam(1)
  val reporterConfig = ReporterConfiguration.fromEnv.withLogSpans(true)
  val tconfig = new Configuration(StreamSettings.appID).withSampler(samplerConfig).withReporter(reporterConfig)
  val tracer =  tconfig.getTracer

  // Optionally register tracer with GlobalTracer
  GlobalTracer.register(tracer);
  val supplier = new TracingKafkaClientSupplier(tracer)
  //new Nothing(tracer)

  // Provide supplier to KafkaStreams
  val wordStream = new KafkaStreams(topology().build(), config, supplier)
  wordStream.start()

  // attach shutdown handler to catch control-c
  sys.ShutdownHookThread {
    wordStream.close(10, TimeUnit.SECONDS)
  }
}
