# Software Architecture and Platforms - a.y. 2023-2024

## Lab #11-20231201 - [Repo](https://github.com/pslab-unibo/sap-2023-2024.git) 

In this lab we see and discuss in practice some patterns and technologies involved in making production-ready microservices as presented in module-2.9. To this purpose, the Cooperative Pixel Art  example introduced in a previous lab [Lab-07-20231027](https://github.com/pslab-unibo/sap-2023-2024/blob/master/Labs/Lab-07-20231027/README.md) is used, extended to include a simple API Gateway service. 

- Health check API
    - Instrumenting microservices with with an Health check API
        - exposing a REST /health endpoint, to get the health check status
        - Status format example: [MicroProfile Health](https://github.com/eclipse/microprofile-health)
    - Who performs the health check?

- Distributed Logging 
    - Instrumenting microservices with a logging adapter
    - Aggregating logs along a logging aggregation pipeline in to a Logging server/service
    - [ELK stack](https://www.elastic.co/elastic-stack), [quick info](https://aws.amazon.com/it/what-is/elk-stack/), [(Logstash in particular)](https://www.elastic.co/logstash)

- Application metrics
    - Instrumenting microservices with a metrics exporter 
    - Accessing data collected on the metrics service
    - [Prometheus platform](https://prometheus.io)
        - [concepts](https://prometheus.io/docs/concepts/data_model/)
            - [metric and label naming](https://prometheus.io/docs/practices/naming/)
            - [getting started](https://prometheus.io/docs/prometheus/latest/getting_started/)
        - metrics service side
            - [installation](https://prometheus.io/docs/prometheus/latest/installation/) 
            - [configuration](https://prometheus.io/docs/prometheus/latest/configuration/configuration/)
            - [querying] https://prometheus.io/docs/prometheus/latest/querying/basics/
        - instrumentation side
            - [instrumentation](https://prometheus.io/docs/instrumenting/clientlibs/)
            - [Java client library](https://github.com/prometheus/client_java)
            - [Quickstart](https://prometheus.github.io/client_java/getting-started/quickstart/)
    - [Grafana](https://grafana.com/) for building dashboards

- Distributed Tracing
    - Instrumenting microservices with a distributed tracing exporter
    - Accessing data collected on the distributed tracing server
    - [Zipkin platform](https://zipkin.io/)
        - [architecture](https://zipkin.io/pages/architecture.html)
        - [data model and API](https://zipkin.io/zipkin-api/#/default/post_spans)
        - [Brave Core Library](https://github.com/openzipkin/brave/blob/master/brave/README.md)

Activity 

- [**Assignment #07**](https://github.com/pslab-unibo/sap-2023-2024/blob/master/Assignments/Assignment-7-20231124.md)


Tools of the day
- [Prometheus platform](https://prometheus.io)
- [Grafana dashboards](https://grafana.com/)
- [Zipkin platform](https://zipkin.io/)


	
	
		
		
		
		