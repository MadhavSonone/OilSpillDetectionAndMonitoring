"use client";

import React from "react";
import { motion } from "framer-motion";
import { X, Anchor, Navigation, Wind, Clock, Map as MapIcon, BarChart3 } from "lucide-react";

interface VesselDetailProps {
  vessel: any;
  onClose: () => void;
}

export const VesselDetailPanel = ({ vessel, onClose }: VesselDetailProps) => {
  if (!vessel) return null;

  return (
    <motion.div
      initial={{ x: "100%" }}
      animate={{ x: 0 }}
      exit={{ x: "100%" }}
      transition={{ type: "spring", damping: 25, stiffness: 200 }}
      className="absolute top-6 right-6 bottom-6 w-96 bg-slate-950/90 backdrop-blur-2xl border border-white/10 rounded-3xl shadow-2xl z-50 flex flex-col overflow-hidden"
    >
      {/* Header */}
      <div className="p-6 border-b border-white/10 flex justify-between items-center">
        <div>
          <h2 className="text-xl font-bold text-white">{vessel.name}</h2>
          <p className="text-xs text-white/40 font-mono tracking-widest">MMSI: {vessel.mmsi}</p>
        </div>
        <button 
          onClick={onClose}
          className="p-2 hover:bg-white/5 rounded-full transition-colors text-white/40 hover:text-white"
        >
          <X size={20} />
        </button>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-y-auto p-6 space-y-6 custom-scrollbar">
        {/* Telemetry Grid */}
        <div className="grid grid-cols-2 gap-4">
          <TelemetryItem icon={<Anchor size={14} />} label="Type" value={vessel.type} />
          <TelemetryItem icon={<Navigation size={14} />} label="Speed" value={`${vessel.sog} kn`} />
          <TelemetryItem icon={<Wind size={14} />} label="Course" value={`${vessel.cog}°`} />
          <TelemetryItem icon={<Clock size={14} />} label="Last Update" value="Just now" />
        </div>

        {/* Anomaly Status */}
        <div className={`p-4 rounded-2xl border ${vessel.sog < 1 ? 'bg-orange-500/10 border-orange-500/20' : 'bg-cyan-500/10 border-cyan-500/20'}`}>
          <div className="flex items-center gap-2 mb-2">
            <div className={`w-2 h-2 rounded-full ${vessel.sog < 1 ? 'bg-orange-400' : 'bg-cyan-400'} animate-pulse`} />
            <span className="text-[10px] font-bold uppercase tracking-widest text-white/80">
              {vessel.sog < 1 ? 'Warning: Loitering Detected' : 'Operational Status: Normal'}
            </span>
          </div>
          <p className="text-xs text-white/60 leading-relaxed">
            {vessel.sog < 1 
              ? "Vessel has maintained near-zero speed in a non-anchorage zone for >20 mins."
              : "Vessel is following standard transit corridor with stable kinematics."}
          </p>
        </div>

        {/* Container Tracking (Lite Mode Mockup) */}
        <div className="space-y-4">
           <h3 className="text-[10px] font-bold uppercase tracking-widest text-white/40">Cargo Manifest & Containers</h3>
           <div className="space-y-2">
              <ContainerItem id="CON-88219" status="Onboard" destination="Rotterdam" />
              <ContainerItem id="CON-44102" status="Discharged" destination="Antwerp" />
              <ContainerItem id="CON-99031" status="In Transit" destination="Hamburg" />
           </div>
        </div>

        {/* Speed Chart Placeholder */}
        <div className="space-y-3">
          <div className="flex justify-between items-center">
            <h3 className="text-[10px] font-bold uppercase tracking-widest text-white/40 flex items-center gap-2">
              <BarChart3 size={12} />
              Speed Profile (Last 2h)
            </h3>
          </div>
          <div className="h-24 w-full bg-white/5 rounded-xl border border-white/5 flex items-end gap-1 p-2">
            {[40, 60, 55, 70, 80, 40, 30, 20, 15, 10].map((h, i) => (
               <div key={i} className="flex-1 bg-cyan-500/20 rounded-t-sm" style={{ height: `${h}%` }} />
            ))}
          </div>
        </div>
      </div>

      {/* Actions */}
      <div className="p-6 border-t border-white/10 flex gap-3">
        <button className="flex-1 py-3 bg-cyan-500 hover:bg-cyan-400 text-slate-950 text-xs font-bold rounded-xl transition-all shadow-lg shadow-cyan-500/20">
          Request Forensics
        </button>
        <button className="p-3 bg-white/5 hover:bg-white/10 text-white border border-white/10 rounded-xl transition-all">
          <MapIcon size={18} />
        </button>
      </div>
    </motion.div>
  );
};

const TelemetryItem = ({ icon, label, value }: any) => (
  <div className="bg-white/5 border border-white/5 p-3 rounded-2xl">
    <div className="flex items-center gap-2 text-white/40 mb-1">
      {icon}
      <span className="text-[10px] font-bold uppercase tracking-widest">{label}</span>
    </div>
    <div className="text-sm font-bold text-white">{value}</div>
  </div>
);

const ContainerItem = ({ id, status, destination }: any) => (
  <div className="flex justify-between items-center p-3 bg-white/5 rounded-xl border border-white/5">
    <div>
      <div className="text-xs font-bold text-white">{id}</div>
      <div className="text-[10px] text-white/40 font-mono">Dest: {destination}</div>
    </div>
    <div className={`px-2 py-0.5 rounded-full text-[9px] font-bold uppercase tracking-tighter ${
      status === 'Onboard' ? 'bg-cyan-500/20 text-cyan-400' : 'bg-white/10 text-white/40'
    }`}>
      {status}
    </div>
  </div>
);
