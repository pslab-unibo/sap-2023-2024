#### Software Architecture and Platforms - a.y. 2023-2024
 
# Assignment #6 - 20231124 - Event-Driven Microservices
- **Description:** The objective of the assignment is to see in practice the design and development of event-driven microservices, including the application of patterns such as Event Sourcing and CQRS. To this purpose:
	- Consider the Cooperative Pixel Art example (Lab-07-20231027). Implement an event-driven version composed by three services:
		- a simple API Gateway, that provides a REST API to Cooperative Pixel Art clients 
		- a PixelGrid microservice, featuring  an event-driven architecture
			- interacting with the API Gateway through event streams
			- exploiting Event Sourcing pattern for persistence 
			- exploiting CQRS pattern to work with Dashboad/View microservice
		- a DashboardView microservice, that allows for querying/observing any PixelGrid
			- exploiting CQRS pattern, as a view aggregating events coming from PixelGrids
	- Further details
		- there could be multiple PixelGrids active at the same time, on different nodes, so that the API Gateway may need to interact with multiple PixelGrid microservices
			- every PixeGrid has its own unique name
		- there is only one DashboardView microservice 
		- user client app interacts only with the API Gateway
			- a user can work on one or multiple pixel grids and can observe one or multiple pixel grids 
	

- **Deliverable**:  
	- Github repo including a report and the source code, organised in a proper way 


 
