CREATE DATABASE leave_db;
USE leave_db;

CREATE TABLE employees (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100),
    department VARCHAR(100)
);

CREATE TABLE leave_balance (
    balance_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT UNIQUE,
    total_leave INT DEFAULT 20,
    used_leave INT DEFAULT 0,
    FOREIGN KEY(employee_id) REFERENCES employees(employee_id)
);

CREATE TABLE leave_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT,
    leave_type VARCHAR(50),
    days INT,
    reason VARCHAR(255),
    status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY(employee_id) REFERENCES employees(employee_id)
);