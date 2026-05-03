"use client";

import { useState, useEffect, useRef } from "react";

export const useIntelligence = () => {
  const [vessels, setVessels] = useState<any[]>([]);
  const [alerts, setAlerts] = useState<any[]>([]);
  const socketRef = useRef<WebSocket | null>(null);

  useEffect(() => {
    // Connect to FastAPI WebSocket
    const socket = new WebSocket("ws://localhost:8000/ws/intelligence");
    socketRef.current = socket;

    socket.onmessage = (event) => {
      const message = JSON.parse(event.data);

      if (message.type === "VESSEL_UPDATE") {
        setVessels((prev) => {
          const index = prev.findIndex((v) => v.mmsi === message.data.mmsi);
          if (index !== -1) {
            const newVessels = [...prev];
            newVessels[index] = { ...newVessels[index], ...message.data, coordinates: [message.data.lon, message.data.lat] };
            return newVessels;
          }
          return [...prev, { ...message.data, coordinates: [message.data.lon, message.data.lat] }];
        });
      }

      if (message.type === "ANOMALY_ALERT") {
        setAlerts((prev) => [message.data, ...prev].slice(0, 50));
      }
    };

    return () => {
      socket.close();
    };
  }, []);

  return { vessels, alerts };
};
