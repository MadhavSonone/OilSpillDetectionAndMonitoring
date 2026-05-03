from fastapi import FastAPI, WebSocket, WebSocketDisconnect, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from typing import List, Dict
import json
import asyncio
import uvicorn
from ai.inference import detector

app = FastAPI(
    title="Aegis-Ocean API",
    description="Maritime Intelligence & Environmental Guardian API",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- State Management ---
# vessel_state[mmsi] = { latest_data, history: [point1, point2, ...] }
vessel_state: Dict[int, dict] = {}
HISTORY_LIMIT = 50

class ConnectionManager:
    def __init__(self):
        self.active_connections: List[WebSocket] = []

    async def connect(self, websocket: WebSocket):
        await websocket.accept()
        self.active_connections.append(websocket)

    def disconnect(self, websocket: WebSocket):
        self.active_connections.remove(websocket)

    async def broadcast(self, message: dict):
        for connection in self.active_connections:
            try:
                await connection.send_json(message)
            except Exception:
                # Handle stale connections
                pass

manager = ConnectionManager()

# --- Endpoints ---

@app.get("/")
async def root():
    return {"status": "online", "system": "Aegis-Ocean", "mode": "Lite"}

@app.post("/api/ingest")
async def ingest_vessel_data(data: dict):
    mmsi = data.get("mmsi")
    if not mmsi: return {"status": "error"}

    # Update Global State
    if mmsi not in vessel_state:
        vessel_state[mmsi] = {"data": data, "history": []}
    
    vessel_state[mmsi]["data"] = data
    
    # Add to trajectory (rolling window)
    new_point = [data["lon"], data["lat"]]
    vessel_state[mmsi]["history"].append(new_point)
    if len(vessel_state[mmsi]["history"]) > HISTORY_LIMIT:
        vessel_state[mmsi]["history"].pop(0)

    # Broadcast update + full history
    await manager.broadcast({
        "type": "VESSEL_UPDATE",
        "data": {
            **data,
            "path": vessel_state[mmsi]["history"]
        }
    })
    return {"status": "received"}

@app.websocket("/ws/intelligence")
async def websocket_endpoint(websocket: WebSocket):
    await manager.connect(websocket)
    try:
        while True:
            # Keep connection alive
            await websocket.receive_text()
    except WebSocketDisconnect:
        manager.disconnect(websocket)

@app.post("/api/detect-spill")
async def detect_spill(file: UploadFile = File(...)):
    contents = await file.read()
    result = detector.detect(contents)
    
    if not result:
        return {"status": "error", "message": "AI Engine not initialized"}
        
    return {
        "status": "success",
        "detections": [result]
    }

# Background Task to simulate data flow if Kafka is not running locally
# In production, a separate consumer script would call manager.broadcast()
async def simulate_data_flow():
    while True:
        await asyncio.sleep(2)
        # Random vessel update example
        await manager.broadcast({
            "type": "VESSEL_UPDATE",
            "data": {
                "mmsi": 111222333,
                "lat": 25.0 + (asyncio.get_event_loop().time() % 10) * 0.01,
                "lon": -90.0,
                "sog": 12.5,
                "cog": 180
            }
        })

# Optional: Run simulator in background
# asyncio.create_task(simulate_data_flow())

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
