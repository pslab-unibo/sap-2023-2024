# Software Architecture and Platforms - a.y. 2023-2024

## Lab #10-20231124 - [Repo](https://github.com/pslab-unibo/sap-2023-2024.git) 

As a follow-up of lectures about [Event-Driven Architecture Style](https://docs.google.com/document/d/1Szif1ksYavi1-AOAm5LRF2pOO-J67udhoGohd_OHwPY/edit?usp=sharing) and Event-Driven Microservices (module-2.8), in this lab we have a look at some technologies useful for concretely design and implement event-driven architectures and event-driven microservices:

- An event broker/event store middleware:  [Apache Kafka](https://docs.google.com/document/d/15QAUpPf8OGb05geq6qKojvrY7zg0OP8B5IMqgEZ4nlI/edit?usp=sharing) 
    - An open-source distributed event streaming platform  
    - [Background and Context](https://developer.confluent.io/faq/apache-kafka/architecture-and-terminology/)
    - [Main Kafka concepts](https://kafka.apache.org/intro)
    - [Kafka Architecture](https://kafka.apache.org/21/documentation/streams/architecture.html)

    - [Kafka Quick start](https://kafka.apache.org/quickstart)
    - [Setting up Kafka Using Docker](https://docs.google.com/document/d/1NKq_YHRi2_VTHSShyvBsFr6BWRwryZOXNW4mHsXrtU4/edit?usp=sharing)
        - using Docker Compose with `kafka-deplo.yaml` config file
    - Working with Kafka - Kafka clients
        - [Kafka clients in Java](https://docs.confluent.io/kafka-clients/java/current/overview.html)
        - A simple Kafka producer and consumer in Java (sources in `sap.kafka` package)
      
- Specifying API in Event-Driven Architectures: Back to the [AsyncAPI](https://www.asyncapi.com/) initiative 
   
- A framework for building event-driven microservices (among the many): [Axon](https://developer.axoniq.io/axon-framework/overview)
    - "Based on architectural principles, such as Domain-Driven Design (DDD) and Command-Query Responsibility Segregation (CQRS), the Axon Framework provides the building blocks that CQRS requires and helps create scalable and extensible applications while maintaining application consistency in distributed systems."
    - [Background - Inspiring View: Event Modelling](https://eventmodeling.org/)
        - [Event Modeling: What is it?](https://eventmodeling.org/posts/what-is-event-modeling/)
    - [Axon Concepts](https://developer.axoniq.io/concepts)
        - DDD, Event Sourcing, CQRS, Microservices
    - [Axon Code Samples](https://developer.axoniq.io/code-samples) 
        - [Hotel demo Example](https://github.com/AxonIQ/hotel-demo) discussed in the article [Event Modeling: What is it?](https://eventmodeling.org/posts/what-is-event-modeling/)  


Activity 

- [**Assignment #06**](https://github.com/pslab-unibo/sap-2023-2024/blob/master/Assignments/Assignment-6-20231124.md)


Tools of the day
- [Apache Kafka](https://docs.docker.com/)
- [Offset Explore / Kafka Tool GUI](https://www.kafkatool.com/)
- [AsyncAPI](https://www.asyncapi.com/)
- [Axon Framework](https://developer.axoniq.io/axon-framework/overview)



	
	
		
		
		
		