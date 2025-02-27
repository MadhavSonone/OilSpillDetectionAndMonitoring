# 🚢 Vessel Monitoring & Oil Spill Detection System 🌊  

## 📌 Project Overview  
This project is designed to track vessels in real-time using **AIS (Automatic Identification System) data** and visualize them on a **map-based UI**. Additionally, it provides an interface for **collecting AIS data, analyzing images, and detecting oil spill regions**.  

The backend is built with **Spring Boot**, while the frontend is developed using **Vaadin** for an interactive UI. **Apache Spark or Apache Storm** is used for handling large-scale AIS data efficiently. The **map-based interface** utilizes **OpenLayers** for seamless visualization.  

---

## 🛠️ Features  
✅ **User Authentication** – Secure login and logout functionality.  
✅ **Interactive Dashboard** – A home page displaying key metrics.  
✅ **Vessel Tracking Map** – Real-time vessel location tracking using AIS data on OpenLayers.  
✅ **AIS Data Collection** – UI for collecting and storing vessel movement data.  
✅ **Oil Spill Detection** – Image processing tools to detect oil spills from satellite images.  

---

## 📌 Tech Stack & Dependencies  

### **Backend:**  
- **Spring Boot** – REST API, backend logic, authentication  
- **Apache Spark / Apache Storm** – Big data processing for AIS data  
- **Spring Security** – Authentication and authorization  

### **Frontend (Vaadin-based UI):**  
- **Vaadin** – UI framework for building web applications  
- **OpenLayers** – Map visualization for tracking vessels  
- **Java 17+** – Required for Vaadin and Spring Boot  

### **Tools & Libraries:**  
- **Maven** – Dependency management  
- **Docker** *(optional)* – Containerized deployment  
- **Eclipse / IntelliJ IDEA** – IDE for development  

---

## ⚡ How to Run the Project  

### **Prerequisites**  
Make sure you have the following installed:  
✅ **Java 17+**  
✅ **Maven**  
✅ **Docker** *(optional, for deployment)*  

### **Steps to Run**  

#### **1️⃣ Clone the Repository**  
```sh
git clone https://github.com/your-username/vessel-monitoring.git
cd vessel-monitoring
```

#### **2️⃣ Set Up the Database**  
- Ensure PostgreSQL is running  
- Create a database: `CREATE DATABASE vessel_tracking;`  
- Configure database settings in `application.properties`  

#### **3️⃣ Build and Run the Backend**  
```sh
cd backend
mvn clean install
mvn spring-boot:run
```

#### **4️⃣ Run the Vaadin Frontend**  
```sh
cd frontend
mvn clean install
mvn spring-boot:run
```

#### **5️⃣ Access the Application**  
- Open a browser and go to `http://localhost:8080/`  
- Login with your credentials and explore the vessel tracking dashboard  

---

## 🛠️ Deployment (Optional)  

To run the project using **Docker**, build and run using:  
```sh
docker-compose up --build
```

---

## 📌 Future Enhancements  
🔹 Add **Machine Learning models** for predicting vessel movement.  
🔹 Improve **Oil Spill Detection** using deep learning models.  
🔹 Implement **real-time notifications** for vessel alerts.  

---

## 📩 Contact  
For any questions or contributions, feel free to reach out or submit a pull request! 🚀  

---

This **README.md** covers everything from **project details, dependencies, setup, and how to run the project**. Let me know if you want any modifications! 🚢💡
