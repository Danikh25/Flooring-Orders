# Flooring Orders

Welcome to the Flooring Orders application! This application allows users to manage flooring orders efficiently by reading and writing them to a database for a company.

## Project Overview

This project follows an enterprise Model-View-Controller (MVC) architecture to ensure scalability, maintainability, and separation of concerns.

### Architecture Overview

- **DTO**: The `dto` package contains classes with data members (properties) that represent the core data structures used in the application.

- **DAO (Data Access Object)**: The `dao` package contains classes responsible for interacting with the database and persisting data. These classes encapsulate the logic for CRUD (Create, Read, Update, Delete) operations.

- **Controller**: The `controller` package contains classes that orchestrate the flow of the application. They handle user requests, invoke appropriate business logic, and manage the overall workflow.

- **UI**: The `UI` package contains classes that interact with the user. These classes are responsible for presenting information to the user and gathering input. The `UserIO` class, along with other view components, handles all console input and output.

- **Service Layer**: The `service` package contains components that implement the service layer. These components encapsulate business logic and perform operations such as validation, calculations, and data transformation.

### User Interaction

The `UserIO` class, along with the view components, facilitates interaction with the user. It handles console input and output, providing a seamless experience for users interacting with the application.

## Development Approach

This project follows an Agile approach to software development, emphasizing iterative development, collaboration, and responsiveness to change. Agile methodologies promote adaptive planning, evolutionary development, early delivery, and continuous improvement.

Feel free to explore the codebase and contribute to the project following the principles of Agile development.

Thank you for using Flooring Orders!
