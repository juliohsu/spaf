# sales2.py

import random
import uuid
from datetime import datetime
from locust import HttpUser, task, between


class SalesUser(HttpUser):
    wait_time = between(0.01, 0.02)  # alvo de ~50-100 req/seg

    @task
    def generate_sale(self):
        # simulação de um "usuário" do sistema
        user = {"username": f"user_{self.locust_id}"}

        sale_data = {
            "transactionId": str(uuid.uuid4()),
            "productId": random.choice(['product1', 'product2', 'product3', 'product4', 'product5', 'product6']),
            "productName": random.choice(['laptop', 'mobile', 'tablet', 'watch', 'headphone', 'speaker']),
            "productCategory": random.choice(['electronic', 'fashion', 'grocery', 'home', 'beauty', 'sports']),
            "productPrice": round(random.uniform(10, 1000), 2),
            "productQuantity": random.randint(1, 10),
            "productBrand": random.choice(['apple', 'samsung', 'oneplus', 'mi', 'boat', 'sony']),
            "currency": random.choice(['USD', 'GBP']),
            "customerId": user["username"],
            "transactionDate": datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%S.%fZ'),
            "paymentMethod": random.choice(['credit_card', 'debit_card', 'online_transfer']),
        }

        self.client.post("/sales", json=sale_data)
