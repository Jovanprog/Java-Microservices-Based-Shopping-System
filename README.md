# Java Microservices-Based Shopping System

## Overview

A distributed online shopping system developed in Java, following a microservices-based architecture. The system consists of a client application, a central REST server, and three independent microservices communicating through Java Message Service (JMS). Each microservice manages its own MySQL database and is responsible for a specific domain of the application.

## System Architecture

The system consists of the following components:

- **Client Application** – A Java SE application that allows users to interact with the system, authenticate, and perform shopping-related operations through REST requests.
- **Central Server** – Processes REST requests from clients and forwards them to the appropriate microservice using JMS. It does not store application data.
- **Microservice 1 – User Management** – Manages users, cities, user information, and account balances.
- **Microservice 2 – Product Management** – Handles product categories, products, pricing, discounts, shopping carts, and wishlists.
- **Microservice 3 – Orders and Transactions** – Manages purchases, orders, order items, and payment transactions.

## Implemented Components

### User Management and Authentication

Supports user registration, authentication, and session management. Users can have multiple roles that determine their permissions within the system. Administrators can manage users, cities, account balances, and delivery information.

### Product and Category Management

Provides functionality for creating and managing product categories and products. Categories support hierarchical relationships through subcategories. Product owners can modify prices and apply discounts to their own products.

### Shopping Cart

Allows users to add products to their shopping carts in specified quantities and remove products when needed. The system maintains the cart's total price and calculates product prices based on applicable discounts.

### Wishlist

Users can maintain wishlists containing products they want to save for future purchases. The system also records when each product is added to a wishlist.

### Orders and Transactions

Supports the complete purchasing process, including order creation, order items, and payment transactions. Each order stores delivery information, creation time, total price, and purchased product details. Transactions record the paid amount and payment timestamp. After a successful purchase, the shopping cart is cleared.

### REST and JMS Communication

The client communicates with the central server through REST endpoints. The central server forwards requests to the appropriate microservice using JMS. Microservices communicate exclusively through JMS, ensuring separation of responsibilities and independent data management.

### Database Management

The system uses three MySQL databases, each responsible for a separate domain:

- User and city management.
- Products, categories, shopping carts, and wishlists.
- Orders, order items, and payment transactions.

## Main Functionalities

- User registration, login, and logout.
- Role-based access control.
- City and user management.
- Account balance management.
- Product and category creation.
- Product price and discount management.
- Shopping cart operations.
- Wishlist management.
- Order creation and payment processing.
- Retrieving users, cities, categories, and products.
- Retrieving shopping carts, wishlists, orders, and transactions.

## Project Structure

- `baze/` – MySQL database scripts, SQL files, and database dumps.
- `java aplikacije/` – Java applications, including the client, central server, and three microservices.
- `uml/sekvenca/` – UML sequence diagrams for the payment process.
- `uml/klase/` – UML class diagrams for each application.

## Technologies Used

- Java SE
- REST API
- Java Message Service (JMS)
- MySQL
- SQL
- UML

## Implementation Status

All required system components and functionalities have been implemented. The client application, central server, and three microservices communicate through the defined REST and JMS interfaces, with each microservice managing its designated database and responsibilities.
