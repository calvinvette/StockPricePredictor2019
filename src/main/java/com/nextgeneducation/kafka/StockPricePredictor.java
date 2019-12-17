package com.nextgeneducation.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;


/**
 * Stock Price Predictor
 *
 */
public class StockPricePredictor {
    public static void main( String[] args ) throws Exception {

        Properties consumerProps = new Properties();
        consumerProps.setProperty("bootstrap.servers", "localhost:9092");
        consumerProps.setProperty("group.id", "test");
        consumerProps.setProperty("enable.auto.commit", "true");
        consumerProps.setProperty("auto.commit.interval.ms", "1000");
        consumerProps.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        Consumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Arrays.asList("stock_prices"));

        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("acks", "all");
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(producerProps);

        boolean done = false;

        while (!done) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
//                Float originalValue = 0F;
                Float originalValue = Float.parseFloat(record.value().split("\t")[1]);
                Float prediction = originalValue * (0.5F + (float) Math.random() * 100 / 100);
                producer.send(new ProducerRecord<String, String>("stock_prices_predictions", record.key(), record.key() + "\t" + Float.toString(prediction)));

            }
            System.out.println("Predicted Prices @" + new Date());
            producer.flush();
            Thread.sleep(5000);
        }

        producer.close();

    }
}
