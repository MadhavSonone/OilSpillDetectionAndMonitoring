# 🛡️ Aegis-Ocean: Global Maritime Intelligence & Environmental Guardian

A high-performance, real-time maritime surveillance system that combines **AISStream.io** data, **Apache Kafka**, **Spark Structured Streaming**, and **LSTM Autoencoders** to detect behavioral anomalies and environmental hazards.

## 🚀 Key Features
- **Real-time Global Tracking**: Live stream integration from `aisstream.io`.
- **Behavioral Anomaly Detection**: "Fintech-grade" LSTM Autoencoders detect erratic ship movements and spoofing.
- **Environmental Guardian**: U-Net based oil spill segmentation from satellite imagery.
- **Premium Command Center**: 3D maritime dashboard built with **Next.js**, **Deck.gl**, and **Framer Motion**.

## 🏗️ Technical Architecture
- **Backbone**: Apache Kafka (Event Streaming)
- **Processing**: Apache Spark (Structured Streaming & Sequence Analysis)
- **Backend**: FastAPI (Python) & PostGIS (Spatial-Temporal Database)
- **AI**: TensorFlow/Keras (LSTM Autoencoders & U-Net)
- **Frontend**: Next.js & TailwindCSS

## 🛠️ Getting Started

### 1. Infrastructure (Docker)
Ensure Docker is running and execute:
```bash
docker-compose up -d
```

### 2. Configuration
Create a `backend/.env` file:
```env
AISSTREAM_API_KEY=your_key_here
DATABASE_URL=postgresql://aegis:aegis_pass@localhost:5432/aegis_ocean
```

### 3. Run the Suite
- **Stream Producer**: `python backend/scripts/aisstream_producer.py`
- **Spark Processor**: `python backend/spark/ais_processor.py`
- **FastAPI Backend**: `python backend/main.py`
- **Frontend**: `cd frontend && npm run dev`

---
*Built for the future of maritime security and ecological preservation.*
