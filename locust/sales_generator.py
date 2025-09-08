from locust import User, task, between
from kafka import KafkaProducer
import json, random, time


class KafkaUser(User):
    # Espera entre tarefas → ~50-100 eventos/s
    wait_time = between(0.01, 0.02)

    def on_start(self):
        # Conexão com o Kafka
        self.producer = KafkaProducer(
            bootstrap_servers="localhost:9092",  
            value_serializer=lambda v: json.dumps(v).encode("utf-8")
        )

    @task
    def send_event(self):
        sale = {
                "productId": "prod_8",
                "productCategory": "electronics",
                "productPrice": 117.01,
                "productQuantity": 1
            }


        # Envia para o tópico do Kafka
        self.producer.send("vendas-simuladas", value=sale)
        print(f"Sent: {sale}")
