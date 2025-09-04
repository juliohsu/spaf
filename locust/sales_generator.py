from locust import HttpUser, task, between

class SalesUser(HttpUser):
    wait_time = between(0.01, 0.02)  # Adjust to target 50-100 requests/sec

    @task
    def generate_sale(self):
        sale_data = {
            "user_id": f"user_{self.locust_id}",
            "product_id": f"prod_{self.locust_id % 100}",
            "price": round(self.locust_id * 0.5 + 10, 2),
            "quantity": self.locust_id % 5 + 1,
            "timestamp": "2025-09-04T07:05:00-03:00"
        }
        self.client.post("/sales", json=sale_data)