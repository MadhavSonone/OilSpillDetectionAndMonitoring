"use client";

import React from "react";
import { Search, Filter, ChevronDown } from "lucide-react";

export const SearchBar = ({ onSearch }: { onSearch: (q: string) => void }) => {
  const [query, setQuery] = React.useState("");

  return (
    <div className="absolute top-6 right-6 z-20 flex items-center gap-2 pointer-events-auto">
      <div className="relative group">
        <div className="absolute inset-y-0 left-4 flex items-center pointer-events-none">
          <Search size={16} className="text-white/20 group-focus-within:text-cyan-400 transition-colors" />
        </div>
        <input 
          type="text" 
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && onSearch(query)}
          placeholder="Search MMSI, Name, or Flag..."
          className="w-80 bg-slate-950/50 backdrop-blur-md border border-white/5 rounded-2xl py-3 pl-12 pr-4 text-sm text-white placeholder:text-white/20 focus:outline-none focus:ring-1 focus:ring-cyan-500/50 transition-all"
        />
      </div>

      <button className="p-3 bg-slate-950/50 backdrop-blur-md border border-white/5 rounded-2xl text-white/40 hover:text-white hover:bg-white/5 transition-all flex items-center gap-2">
        <Filter size={16} />
        <span className="text-[10px] font-bold uppercase tracking-widest hidden md:block">Filter</span>
        <ChevronDown size={12} className="opacity-40" />
      </button>
    </div>
  );
};
