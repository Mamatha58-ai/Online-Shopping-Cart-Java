# Online Shopping Cart 🛒

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-orange.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A comprehensive Java Swing desktop application for online shopping, featuring product browsing, shopping cart management, and administrative controls with MySQL database integration.

## 📋 Table of Contents

- [Features](#features)
- [Screenshots](#screenshots)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Usage](#usage)
- [Default Credentials](#default-credentials)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## ✨ Features

### User Features
- 🔐 **Role-based Authentication**: Separate login for Admin and User roles
- 📝 **User Registration**: Sign-up functionality with password management
- 🛍️ **Product Browsing**: Browse products with image previews and search functionality
- 🛒 **Shopping Cart**: Add/remove items, update quantities, and checkout
- 🎫 **Coupon System**: Support for discount codes (`SAVE10`, `SAVE20`, `WELCOME`, `FESTIVAL50`)
- 📊 **Order Management**: Place orders and track purchases

### Admin Features
- 📦 **Product Management**: View, add, update stock, and delete products
- 👥 **User Management**: View and manage user accounts
- 📈 **Dashboard**: Administrative overview and controls

### Technical Features
- 💾 **MySQL Integration**: Robust database-backed data storage
- 🖼️ **Image Support**: Product images stored locally
- 🔄 **Data Synchronization**: Mix of database and local file storage

## 📸 Screenshots

*Add screenshots of your application here to showcase the user interface and features.*

<!-- Example:
![Login Screen](screenshots/login.png)
![Product Catalog](screenshots/products.png)
![Admin Dashboard](screenshots/admin.png)
-->

## 🛠️ Tech Stack

- **Language**: Java 17+
- **GUI Framework**: Java Swing
- **Database**: MySQL 8.0+
- **Database Connector**: MySQL Connector/J 9.6.0
- **IDE**: Eclipse (recommended)
- **Build Tool**: Manual compilation

## 📋 Prerequisites

Before running this application, ensure you have the following installed:

- **Java JDK 17+**: [Download from Oracle](https://www.oracle.com/java/technologies/javase-downloads.html)
- **MySQL Server 8.0+**: [Download from MySQL](https://dev.mysql.com/downloads/mysql/)
- **MySQL Workbench** (optional, for database management)

## 🚀 Installation

### Clone the Repository

```bash
git clone https://github.com/Mamatha58-ai/Online-Shopping-Cart-Java.git
cd Online-Shopping-Cart-Java
```

### Database Configuration

Update database credentials in the following files if your MySQL setup differs:

- `src/DatabaseSetup.java`
- `src/DatabaseProductStore.java`

Current default settings:
- **Host**: `localhost`
- **Port**: `3306`
- **Database**: `shopping_cart`
- **Username**: `root`
- **Password**: `1008`

## 🗄️ Database Setup

1. **Start MySQL Server** on your local machine
2. **Run Database Setup**:

   ### In Eclipse:
   - Import the project
   - Ensure `lib/mysql-connector-j-9.6.0.jar` is in the build path
   - Run `DatabaseSetup.java`

   ### From Command Line:
   ```powershell
   # Compile
   javac -cp "lib/mysql-connector-j-9.6.0.jar" -d bin src\*.java

   # Setup database
   java -cp "bin;lib/mysql-connector-j-9.6.0.jar" DatabaseSetup
   ```

This will create the database, tables, and insert sample data.

## 💻 Usage

### Running the Application

#### In Eclipse:
1. Import as existing Java project
2. Confirm MySQL connector JAR is attached
3. Run `Main.java`

#### From Command Line:
```powershell
# Start the application
java -cp "bin;lib/mysql-connector-j-9.6.0.jar" Main
```

### Application Workflow

1. **Login**: Choose Admin or User role
2. **Browse Products**: View catalog with images and search
3. **Add to Cart**: Select items and quantities
4. **Checkout**: Apply coupons and complete purchase
5. **Admin Panel**: Manage products and users (Admin only)

## 🔑 Default Credentials

### Admin Account
- **Username**: `admin`
- **Password**: `admin123`

### Test User Account
- **Username**: `testuser`
- **Password**: `test123`

## 📁 Project Structure

```
OnlineShoppingCart_finish/
├── src/                    # Java source files
│   ├── Main.java          # Application entry point
│   ├── LoginFrame.java    # Authentication interface
│   ├── UserDashboard.java # User shopping interface
│   ├── AdminDashboard.java # Admin management interface
│   ├── DatabaseSetup.java # Database initialization
│   └── ...                # Other Java classes
├── bin/                   # Compiled class files
├── lib/                   # External libraries
│   └── mysql-connector-j-9.6.0.jar
├── images/                # Product images
├── products.txt           # Product data (legacy)
├── users.txt             # User data (legacy)
├── discounts.txt         # Discount codes
└── README.md             # This file
```

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

### Development Guidelines

- Follow Java naming conventions
- Add comments for complex logic
- Test thoroughly before submitting
- Update documentation as needed

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Contact

**Mamatha58-ai** - [GitHub Profile](https://github.com/Mamatha58-ai)

Project Link: [https://github.com/Mamatha58-ai/Online-Shopping-Cart-Java](https://github.com/Mamatha58-ai/Online-Shopping-Cart-Java)

---

⭐ **Star this repo** if you found it helpful!
