import asyncio
import websockets
import json
import os
from dotenv import load_dotenv

load_dotenv()

API_KEY = os.getenv("AISSTREAM_API_KEY")

async def test_connection():
    if not API_KEY or API_KEY == "<YOUR_API_KEY>":
        print("[ERROR] AISSTREAM_API_KEY not found in .env file.")
        return

    subscribe_message = {
        "APIKey": API_KEY,
        "BoundingBoxes": [[[-180, -90], [180, 90]]], # Global test
        "FilterMessageTypes": ["PositionReport"]
    }

    print(f"Connecting to AISStream with key: {API_KEY[:5]}***")
    
    try:
        async with websockets.connect("wss://stream.aisstream.io/v0/stream") as websocket:
            await websocket.send(json.dumps(subscribe_message))
            print("[SUCCESS] Subscription sent. Waiting for data...")

            count = 0
            async for message in websocket:
                ais_data = json.loads(message)
                print(f"\n--- Message {count + 1} ---")
                print(json.dumps(ais_data, indent=2))
                
                count += 1
                if count >= 5:
                    break
            
            print("\n[SUCCESS] Test completed successfully.")
            
    except Exception as e:
        print(f"[ERROR] Connection failed: {e}")

if __name__ == "__main__":
    asyncio.run(test_connection())
