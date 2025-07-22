CREATE DATABASE IF NOT EXISTS learning_app;
USE learning_app;

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    role ENUM('MEMBER', 'ADMIN') DEFAULT 'MEMBER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Classes table
CREATE TABLE classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) DEFAULT 0,
    instructor VARCHAR(100),
    duration VARCHAR(50),
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Modules table
CREATE TABLE modules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    sequence_order INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
);

-- Materials table
CREATE TABLE materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    content TEXT,
    video_url VARCHAR(500),
    file_url VARCHAR(500),
    sequence_order INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE CASCADE
);

-- Purchases table
CREATE TABLE purchases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'REJECTED') DEFAULT 'PENDING',
    payment_proof VARCHAR(500),
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
);

-- Feedback table
CREATE TABLE feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
);

-- Insert sample admin user
INSERT INTO users (username, email, password, full_name, role) VALUES 
('admin', 'admin@learning.com', 'admin123', 'Administrator', 'ADMIN');

-- Insert sample classes
INSERT INTO classes (name, description, price, instructor, duration, image_url) VALUES 
('Java Programming Basics', 'Learn Java from scratch with hands-on examples', 299000, 'John Doe', '40 hours', 'https://images.pexels.com/photos/1181671/pexels-photo-1181671.jpeg'),
('Web Development with Spring Boot', 'Build web applications using Spring Boot framework', 499000, 'Jane Smith', '60 hours', 'https://images.pexels.com/photos/1181673/pexels-photo-1181673.jpeg'),
('Database Design Fundamentals', 'Master database design and SQL queries', 399000, 'Mike Johnson', '45 hours', 'https://images.pexels.com/photos/1181675/pexels-photo-1181675.jpeg');

-- Insert sample modules
INSERT INTO modules (class_id, name, description, sequence_order) VALUES 
(1, 'Introduction to Java', 'Basic concepts and setup', 1),
(1, 'Variables and Data Types', 'Understanding Java data types', 2),
(1, 'Control Structures', 'Loops and conditional statements', 3),
(2, 'Spring Boot Basics', 'Introduction to Spring Boot', 1),
(2, 'Building REST APIs', 'Creating web services', 2),
(3, 'Database Concepts', 'Understanding databases', 1),
(3, 'SQL Fundamentals', 'Basic SQL operations', 2);

-- Insert sample materials
INSERT INTO materials (module_id, name, content, video_url, sequence_order) VALUES 
(1, 'What is Java?', 'Java is a programming language...', 'https://www.youtube.com/watch?v=sample1', 1),
(1, 'Setting up Development Environment', 'Install JDK and IDE...', 'https://www.youtube.com/watch?v=sample2', 2),
(2, 'Primitive Data Types', 'int, double, boolean, char...', 'https://www.youtube.com/watch?v=sample3', 1),
(2, 'Reference Data Types', 'String, Arrays, Objects...', 'https://www.youtube.com/watch?v=sample4', 2);