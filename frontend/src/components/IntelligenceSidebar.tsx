"use client";

import React from "react";
import { AlertTriangle, Info, Ship, ShieldAlert } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";

interface Alert {
  id: string;
  type: "ANOMALY" | "SPILL" | "SECURITY";
  severity: "CRITICAL" | "WARNING" | "INFO";
  vessel: string;
  reason: string;
  timestamp: string;
}

interface IntelligenceSidebarProps {
  alerts: any[];
}

export const IntelligenceSidebar = ({ alerts }: IntelligenceSidebarProps) => {
  return (
    <div className="w-96 h-full bg-slate-950/80 backdrop-blur-xl border-r border-white/10 p-6 flex flex-col gap-6 text-white overflow-hidden shadow-2xl">
      <div className="flex items-center gap-3 border-b border-white/10 pb-4">
        <ShieldAlert className="text-cyan-400 w-6 h-6" />
        <h2 className="text-xl font-bold tracking-tight">Intelligence Feed</h2>
      </div>

      <div className="flex-1 overflow-y-auto space-y-4 pr-2 custom-scrollbar">
        <AnimatePresence>
          {alerts.map((alert) => (
            <motion.div
              key={alert.id}
              initial={{ x: -20, opacity: 0 }}
              animate={{ x: 0, opacity: 1 }}
              className={`p-4 rounded-xl border ${
                alert.severity === "CRITICAL"
                  ? "bg-red-500/10 border-red-500/30"
                  : "bg-amber-500/10 border-amber-500/30"
              } hover:bg-white/5 transition-colors cursor-pointer group`}
            >
              <div className="flex justify-between items-start mb-2">
                <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${
                  alert.severity === "CRITICAL" ? "border-red-500/50 text-red-400" : "border-amber-500/50 text-amber-400"
                }`}>
                  {alert.type}
                </span>
                <span className="text-[10px] text-white/40">{alert.timestamp}</span>
              </div>
              <h3 className="font-semibold text-sm mb-1 group-hover:text-cyan-400 transition-colors">
                {alert.vessel}
              </h3>
              <p className="text-xs text-white/60 leading-relaxed italic">
                "{alert.reason}"
              </p>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>

      <div className="pt-4 border-t border-white/10">
        <div className="flex justify-between text-[10px] text-white/40 uppercase tracking-widest font-bold">
          <span>System Status:</span>
          <span className="text-green-400 animate-pulse">Live</span>
        </div>
      </div>
    </div>
  );
};
