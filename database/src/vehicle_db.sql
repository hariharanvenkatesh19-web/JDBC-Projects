CREATE DATABASE vehicle_db;
USE vehicle_db;

CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    phone VARCHAR(15),
    email VARCHAR(100)
);

CREATE TABLE vehicles (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    vehicle_number VARCHAR(30) UNIQUE,
    vehicle_model VARCHAR(100),
    vehicle_type VARCHAR(50),
    FOREIGN KEY(customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE service_records (
    service_id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_id INT,
    service_date DATE,
    service_type VARCHAR(100),
    amount DECIMAL(10,2),
    status VARCHAR(30),
    FOREIGN KEY(vehicle_id) REFERENCES vehicles(vehicle_id)
);