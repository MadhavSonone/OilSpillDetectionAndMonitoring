"use client";

import React, { useState } from "react";
import { IntelligenceSidebar } from "@/components/IntelligenceSidebar";
import { VesselMap } from "@/components/VesselMap";
import { VesselDetailPanel } from "@/components/VesselDetailPanel";
import { DetectionControl } from "@/components/DetectionControl";
import { SearchBar } from "@/components/SearchBar";
import { useIntelligence } from "@/hooks/useIntelligence";
import { Shield, Radio, Activity, Database } from "lucide-react";
import { AnimatePresence } from "framer-motion";

export default function DashboardPage() {
  const [selectedVessel, setSelectedVessel] = useState<any>(null);
  const { vessels, alerts } = useIntelligence();

  return (
    <main className="flex h-screen w-screen bg-slate-950 font-sans selection:bg-cyan-500/30 overflow-hidden">
      {/* Sidebar */}
      <IntelligenceSidebar alerts={alerts} />

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col relative">

        {/* Header */}
        <header className="absolute top-0 left-0 right-0 z-10 p-6 flex justify-between items-center pointer-events-none">
          <div className="flex items-center gap-4 bg-slate-950/50 backdrop-blur-md p-2 px-4 rounded-2xl border border-white/5 pointer-events-auto">
            <div className="flex items-center gap-2">
              <Shield className="text-cyan-400 w-5 h-5" />
              <span className="text-sm font-bold tracking-[0.2em] text-white uppercase">Aegis-Ocean</span>
            </div>
            <div className="h-4 w-px bg-white/10 mx-2" />
            <div className="flex gap-4">
              <StatusBadge icon={<Radio size={12} />} label="Kafka" status="active" />
              <StatusBadge icon={<Activity size={12} />} label="Spark" status="active" />
              <StatusBadge icon={<Database size={12} />} label="PostGIS" status="active" />
            </div>
          </div>

          <div className="flex items-center gap-4 bg-slate-950/50 backdrop-blur-md p-2 px-4 rounded-2xl border border-white/5 pointer-events-auto">
            <div className="flex flex-col items-end">
              <span className="text-[10px] text-white/40 font-bold uppercase tracking-widest">Active Vessels</span>
              <span className="text-sm font-mono font-bold text-cyan-400">1,248</span>
            </div>
          </div>
        </header>

        {/* Environmental Control */}
        <DetectionControl />

        {/* Global Search */}
        <SearchBar onSearch={(query: string) => {
          const found = vessels.find(v => 
            v.mmsi.toString() === query || 
            v.vessel_name?.toLowerCase().includes(query.toLowerCase())
          );
          if (found) {
            setSelectedVessel(found);
            // In a real app, we would also emit a map-center event here
          }
        }} />

        {/* Map */}
        <VesselMap
          vessels={vessels}
          onVesselSelect={(v: any) => setSelectedVessel(v)}
        />

        {/* Vessel Detail Panel */}
        <AnimatePresence>
          {selectedVessel && (
            <VesselDetailPanel
              vessel={selectedVessel}
              onClose={() => setSelectedVessel(null)}
            />
          )}
        </AnimatePresence>

        {/* Overlay Decoration */}
        <div className="absolute bottom-6 left-6 right-6 flex justify-between items-end pointer-events-none">
          <div className="bg-slate-950/80 backdrop-blur-md border border-white/10 p-4 rounded-2xl pointer-events-auto">
            <h4 className="text-[10px] text-white/40 font-bold uppercase tracking-widest mb-2">Operation Mode</h4>
            <div className="flex gap-2">
              <button className="px-3 py-1 bg-cyan-500/20 text-cyan-400 text-[10px] font-bold rounded-lg border border-cyan-500/30">Surveillance</button>
              <button className="px-3 py-1 bg-white/5 text-white/40 text-[10px] font-bold rounded-lg border border-white/10">Response</button>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}

function StatusBadge({ icon, label, status }: { icon: React.ReactNode, label: string, status: 'active' | 'error' }) {
  return (
    <div className="flex items-center gap-1.5 opacity-80">
      <div className={`text-${status === 'active' ? 'cyan-400' : 'red-400'}`}>
        {icon}
      </div>
      <span className="text-[9px] font-bold uppercase tracking-wider text-white/60">{label}</span>
      <div className={`w-1 h-1 rounded-full ${status === 'active' ? 'bg-cyan-400' : 'bg-red-400'} animate-pulse`} />
    </div>
  );
}
