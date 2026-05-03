import asyncio
import websockets
import json
import os
import httpx
from dotenv import load_dotenv
load_dotenv()

# Configuration
API_KEY = os.environ.get("AISSTREAM_API_KEY")
BACKEND_URL = "http://localhost:8000/api/ingest"

async def connect_ais_stream():
    if not API_KEY:
        print("[ERROR] AISSTREAM_API_KEY missing from .env")
        return

    subscribe_message = {
        "APIKey": API_KEY,
        "BoundingBoxes": [[[-180, -90], [180, 90]]], 
        "FilterMessageTypes": ["PositionReport"]
    }

    print(f"[INFO] Connecting to Aegis-Ocean (Lite Mode)...")
    
    async with httpx.AsyncClient() as client:
        try:
            async with websockets.connect("wss://stream.aisstream.io/v0/stream") as websocket:
                await websocket.send(json.dumps(subscribe_message))
                print("[INFO] Live stream established. Routing to Dashboard...")

                async for message in websocket:
                    try:
                        ais_data = json.loads(message)
                        payload = {
                            "mmsi": ais_data["MetaData"]["MMSI"],
                            "vessel_name": ais_data["MetaData"].get("ShipName", "Unknown").strip(),
                            "lat": ais_data["MetaData"]["latitude"],
                            "lon": ais_data["MetaData"]["longitude"],
                            "sog": ais_data["Message"]["PositionReport"]["Sog"],
                            "cog": ais_data["Message"]["PositionReport"]["Cog"],
                            "timestamp": ais_data["MetaData"]["time_utc"]
                        }
                        
                        # Direct Ingestion to Backend
                        await client.post(BACKEND_URL, json=payload)
                        
                    except Exception:
                        pass
        except Exception as e:
            print(f"[ERROR] Stream connection failed: {e}")

if __name__ == "__main__":
    asyncio.run(connect_ais_stream())
