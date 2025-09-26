# __Section 1 : Architecture Summary__


This Spring Boot application combines both MVC and REST architectures to handle different parts of the system efficiently. The Admin and Doctor dashboards are implemented using Thymeleaf templates, providing a dynamic and interactive user interface, while all other modules—such as patient management, appointments, and prescriptions—are exposed through RESTful APIs for easier integration and scalability. The application interacts with two databases: MySQL stores structured data including patients, doctors, appointments, and admin information, whereas MongoDB is used for unstructured or semi-structured data like prescriptions.

All incoming requests are routed through a common service layer, which contains the core business logic and ensures proper separation of concerns. The service layer interacts with repositories tailored to each database type: JPA entities manage MySQL data, while document models handle MongoDB collections. This architecture allows the application to leverage the strengths of both relational and non-relational databases, maintain clean code organization, and scale individual modules as needed.



# __Section 2 : Numbered flow of data and control__


1. A user (Admin, Doctor, or Patient) accesses a dashboard or module, either via a Thymeleaf-based web page or a REST API endpoint.

2. The request is received by the appropriate controller—MVC controllers for Thymeleaf pages, REST controllers for API requests.

3. The controller delegates the request to the service layer, which contains the business logic for processing data and handling rules.

4. The service layer determines which repository or database to interact with based on the type of data being accessed (MySQL for structured data, MongoDB for prescriptions).

5. The repository layer executes queries or operations on the respective database, using JPA entities for MySQL or document models for MongoDB.

6. The results from the database are returned to the service layer, which processes or transforms the data as needed.

7. Finally, the service layer sends the processed data back to the controller, which then renders a Thymeleaf template for the user or returns a JSON response for API calls.
