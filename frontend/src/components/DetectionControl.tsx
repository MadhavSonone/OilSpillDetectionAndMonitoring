"use client";

import React from "react";
import { Search, Upload, Target, Droplets, Layers, ExternalLink } from "lucide-react";
import { motion } from "framer-motion";

const activeSpills = [
  { id: "S-102", area: "12.5 sq km", confidence: "94%", lat: "28.5", lon: "-89.2", timestamp: "1h ago" },
  { id: "S-105", area: "2.1 sq km", confidence: "88%", lat: "27.1", lon: "-90.5", timestamp: "15m ago" },
];

export const DetectionControl = () => {
  return (
    <div className="absolute top-24 left-6 w-80 bg-slate-950/50 backdrop-blur-md border border-white/5 rounded-3xl p-6 flex flex-col gap-6 z-20 overflow-hidden">
      <div className="flex items-center gap-3">
        <Droplets className="text-red-400 w-5 h-5" />
        <h3 className="text-sm font-bold tracking-widest text-white uppercase">Environmental Scan</h3>
      </div>

      {/* Manual Upload Section */}
      <div className="space-y-3">
         <p className="text-[10px] font-bold text-white/30 uppercase tracking-tighter">Imagery Analysis</p>
         <button className="w-full group flex items-center justify-center gap-2 p-4 bg-white/5 hover:bg-white/10 border border-dashed border-white/20 rounded-2xl transition-all">
            <Upload size={16} className="text-white/40 group-hover:text-cyan-400" />
            <span className="text-xs text-white/60 font-medium">Upload Satellite Metadata</span>
         </button>
      </div>

      {/* Active Detections */}
      <div className="space-y-4">
        <div className="flex justify-between items-center">
            <p className="text-[10px] font-bold text-white/30 uppercase tracking-tighter">Active Spill Zones</p>
            <span className="text-[10px] text-red-400 font-bold bg-red-400/10 px-2 py-0.5 rounded-full">2 Active</span>
        </div>
        
        <div className="space-y-2">
          {activeSpills.map((spill) => (
            <motion.div 
              key={spill.id}
              whileHover={{ x: 5 }}
              className="p-3 bg-white/5 border border-white/5 rounded-xl flex flex-col gap-1 cursor-pointer group"
            >
              <div className="flex justify-between items-center">
                 <span className="text-xs font-bold text-white group-hover:text-red-400 transition-colors">Zone {spill.id}</span>
                 <span className="text-[9px] text-white/40">{spill.timestamp}</span>
              </div>
              <div className="flex items-center justify-between mt-1">
                 <div className="flex items-center gap-1.5">
                    <Target size={10} className="text-white/20" />
                    <span className="text-[10px] text-white/60 font-mono">{spill.lat}, {spill.lon}</span>
                 </div>
                 <div className="flex items-center gap-1">
                    <Layers size={10} className="text-white/20" />
                    <span className="text-[10px] text-white/60">{spill.area}</span>
                 </div>
              </div>
              <div className="mt-2 h-1 w-full bg-white/5 rounded-full overflow-hidden">
                 <div className="h-full bg-red-500/40 rounded-full" style={{ width: spill.confidence }} />
              </div>
            </motion.div>
          ))}
        </div>
      </div>

      {/* Quick Filters */}
      <div className="flex gap-2">
        <button className="flex-1 p-2 bg-white/5 hover:bg-white/10 rounded-lg text-[9px] font-bold text-white/60 transition-colors border border-white/5">SAR Data</button>
        <button className="flex-1 p-2 bg-white/5 hover:bg-white/10 rounded-lg text-[9px] font-bold text-white/60 transition-colors border border-white/5">Optical</button>
      </div>
    </div>
  );
};
