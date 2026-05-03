import json
import time
import random
from datetime import datetime
from confluent_kafka import Producer

# Kafka Configuration
KAFKA_BROKER = "localhost:9092"
TOPIC = "ais-raw"

# Mock Vessel Data
VESSELS = [
    {"mmsi": 111222333, "name": "Ocean Voyager", "type": "Cargo"},
    {"mmsi": 444555666, "name": "Sea Guardian", "type": "Tanker"},
    {"mmsi": 777888999, "name": "Northern Star", "type": "Fishing"},
    {"mmsi": 123456789, "name": "Black Pearl", "type": "Pleasure"},
]

# Initial Locations (Gulf of Mexico area)
locations = {
    v["mmsi"]: {"lat": 25.0 + random.uniform(-1, 1), "lon": -90.0 + random.uniform(-1, 1)}
    for v in VESSELS
}

def delivery_report(err, msg):
    if err is not None:
        print(f"Message delivery failed: {err}")
    else:
        print(f"Message delivered to {msg.topic()} [{msg.partition()}]")

def generate_ais_message(vessel):
    mmsi = vessel["mmsi"]
    loc = locations[mmsi]
    
    # Simulate movement
    loc["lat"] += random.uniform(-0.001, 0.001)
    loc["lon"] += random.uniform(-0.001, 0.001)
    
    # Random speed and course
    sog = random.uniform(5, 25)
    cog = random.uniform(0, 360)
    
    return {
        "mmsi": mmsi,
        "vessel_name": vessel["name"],
        "vessel_type": vessel["type"],
        "lat": loc["lat"],
        "lon": loc["lon"],
        "sog": sog,
        "cog": cog,
        "timestamp": datetime.utcnow().isoformat()
    }

def main():
    p = Producer({"bootstrap.servers": KAFKA_BROKER})
    
    print(f"Starting AIS simulation on topic: {TOPIC}")
    
    try:
        while True:
            for vessel in VESSELS:
                msg = generate_ais_message(vessel)
                p.produce(
                    TOPIC, 
                    json.dumps(msg).encode('utf-8'), 
                    callback=delivery_report
                )
            p.flush()
            time.sleep(2)  # Emit every 2 seconds
    except KeyboardInterrupt:
        print("Simulation stopped.")

if __name__ == "__main__":
    main()
