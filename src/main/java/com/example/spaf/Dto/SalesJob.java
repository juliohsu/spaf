package com.example.spaf;

import com.example.spaf.Dto.Transaction;
import com.example.spaf.Dto.SalesPerCategory;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Date;
import java.util.Properties;

public class SalesJob {
    public static void main(String[] args) throws Exception {
        
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Config Kafka
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "kafka:29092");
        props.setProperty("group.id", "flink-sales");

        FlinkKafkaConsumer<String> consumer =
            new FlinkKafkaConsumer<>("vendas-simuladas", new SimpleStringSchema(), props);

        DataStream<String> stream = env.addSource(consumer);

        // Converter JSON -> Transaction
        ObjectMapper mapper = new ObjectMapper();
        SingleOutputStreamOperator<Transaction> transactions = stream.map(
            (MapFunction<String, Transaction>) value -> mapper.readValue(value, Transaction.class)
        );

        // Map Transaction -> SalesPerCategory
        SingleOutputStreamOperator<SalesPerCategory> agg = transactions
            .map(t -> new SalesPerCategory(
                    new Date(System.currentTimeMillis()),   // transactionDate
                    t.getProductCategory(),                 // categoria
                    t.getProductPrice() * t.getProductQuantity() // total de vendas
            ))
            .returns(TypeInformation.of(SalesPerCategory.class));

        // Sink PostgreSQL
        agg.addSink(
            JdbcSink.sink(
                "INSERT INTO sales_summary (transactionDate, category, totalSales) VALUES (?, ?, ?)",
                (ps, spc) -> {
                    ps.setDate(1, spc.getTransactionDate());
                    ps.setString(2, spc.getCategory());
                    ps.setDouble(3, spc.getTotalSales());
                },
                JdbcExecutionOptions.builder()
                    .withBatchSize(1000)
                    .withBatchIntervalMs(200)
                    .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                    .withDriverName("org.postgresql.Driver")
                    .withUrl("jdbc:postgresql://postgres:5432/postgres") // se Flink rodar em container
                    .withUsername("postgres")
                    .withPassword("postgres")
                    .build()
            )
        );

        env.execute("Sales Job with Flink + PostgreSQL");
    }
}
