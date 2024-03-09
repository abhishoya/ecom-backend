# E-Commerce Backend

Tech Stack:
- Java with Spring
- MongoDB
- Kafka
- Grafana - Loki (Logs) + Tempo (Traces)

[![GitHub issues](https://img.shields.io/github/issues/abhishoya/ecom-backend?style=for-the-badge)](https://github.com/abhishoya/ecom-backend/issues) [![GitHub forks](https://img.shields.io/github/forks/abhishoya/ecom-backend?style=for-the-badge)](https://github.com/abhishoya/ecom-backend/network) [![GitHub stars](https://img.shields.io/github/stars/abhishoya/ecom-backend?style=for-the-badge)](https://github.com/abhishoya/ecom-backend/stargazers) [![GitHub license](https://img.shields.io/github/license/abhishoya/ecom-backend?style=for-the-badge)](https://github.com/abhishoya/ecom-backend/blob/master/LICENSE)

# Project Description

The project aims to provide a generic microservices backend for e-Commerce platforms. It has features to create, update, and delete products. It also has feature for admin to manage users and orders. The application also has a payment service which can be configured to accept webhooks from different payment gateways such as Razorpay.

# Services

## Gateway Service

This service serves as an entrypoint to the application. It intercepts incoming requests, perform authentication, and forward request to relevant service.

## Auth Service:

- SignUp
- Login
- Authorization (JWT Verification)

## User Service:

- Get User
- Update User
- Delete User
- Admin Only
    - List Users 

## Order Service

- Create Order
- Get Orders
- Cancel Order
- Admin Only
    - List Orders
    - Update Status For Order

## Product Service:

- Get all products by page number

- Admin Only
    - Create Product
    - Update Product
    - Delete Product

## Payment Service 

- Record Payment Webhook

