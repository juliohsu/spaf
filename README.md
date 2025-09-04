# ARQUITETURA DE SOFTWARE - 25.2

## Stream Processing com Apache Flink (SPAF)

![Apache Flink E-Commerce Analytics with Elasticsearch and Postgres](architecture_img.png)

### 1. O que vai ser feito?
Vamos construir um pipeline que processa dados de vendas simuladas em tempo real para gerar métricas e análises imediatas. Os eventos transitam por Kafka, são processados pelo Apache Flink, e os resultados ficam disponíveis para consulta e visualização.

### 2. Como vai ser feito?
- **Geração de dados**: Locust Script Python gera vendas fictícias (50 a 100 eventos por segundo).
- **Ingestão e transporte**: Os eventos vão para um tópico do Apache Kafka.
- **Processamento**: Apache Flink consome os dados, faz agregações (vendas por categoria, detecção de anomalias).
- **Armazenamento**: Resultados salvos em PostgreSQL.

### 3. Métricas a serem avaliadas
- Latência entre geração e visualização dos dados (end-to-end delay).
- Throughput do sistema (eventos processados por segundo).
- Consumo de recursos (CPU e memória) dos componentes principais (Kafka, Flink).
- Precisão e consistência das agregações e detecções feitas pelo Flink.

### 4. Análise e apresentação dos resultados
- Apresentar gráficos de latência e throughput em diferentes cargas (ex: 10, 50, 100 eventos/s).
- Apresentação sobre as ferramentas utilizadas no projeto.
- Discorrer sobre a arquitetura, possíveis falhas e melhorias futuras.