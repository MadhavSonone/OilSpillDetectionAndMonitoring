"use client";

import React, { useState } from "react";
import Map from "react-map-gl/maplibre";
import DeckGL from "deck.gl";
import { IconLayer, TextLayer, PathLayer } from "@deck.gl/layers";
import "maplibre-gl/dist/maplibre-gl.css";

const INITIAL_VIEW_STATE = {
  longitude: -90.0,
  latitude: 25.0,
  zoom: 5,
  pitch: 45,
  bearing: 0,
};

// High-quality free dark style for MapLibre
const FREE_MAP_STYLE = "https://basemaps.cartocdn.com/gl/dark-matter-gl-style/style.json";

interface VesselMapProps {
  vessels: any[];
  onVesselSelect: (vessel: any) => void;
}

export const VesselMap = ({ vessels, onVesselSelect }: VesselMapProps) => {
  const [viewState, setViewState] = useState(INITIAL_VIEW_STATE);

  const layers = [
    new PathLayer({
      id: "path-layer",
      data: vessels.filter(v => v.path && v.path.length > 1),
      getPath: (d: any) => d.path,
      getColor: (d: any) => [34, 211, 238, 100], // Faded Cyan
      getWidth: 2,
      widthMinPixels: 1,
      pickable: false
    }),
    new IconLayer({
      id: "vessel-layer",
      data: vessels,
      pickable: true,
      onHover: ({ object }) => {
        // Optional: handle hover
      },
      onClick: ({ object }) => {
        if (object) onVesselSelect(object);
      },
      iconAtlas: "https://raw.githubusercontent.com/visgl/deck.gl-data/master/website/icon-atlas.png",
      iconMapping: {
        marker: { x: 0, y: 0, width: 128, height: 128, anchorY: 128, mask: true }
      },
      getIcon: (d: any) => "marker",
      getPosition: (d: any) => d.coordinates || [d.lon, d.lat],
      getSize: (d: any) => 30,
      getColor: (d: any) => d.sog < 1 ? [255, 150, 0] : [0, 255, 255], 
    }),
    new TextLayer({
      id: "text-layer",
      data: vessels,
      getPosition: (d: any) => d.coordinates || [d.lon, d.lat],
      getText: (d: any) => d.vessel_name || d.name,
      getSize: 12,
      getAngle: 0,
      getTextAnchor: "middle",
      getAlignmentBaseline: "top",
      getColor: [255, 255, 255, 200],
      getPixelOffset: [0, 15]
    })
  ];

  return (
    <div className="flex-1 relative bg-slate-900 overflow-hidden">
      <DeckGL
        initialViewState={viewState}
        onViewStateChange={(e: any) => setViewState(e.viewState)}
        controller={true}
        layers={layers}
        getTooltip={({ object }: any) => object && `${object.vessel_name || object.name}\nMMSI: ${object.mmsi}\nSpeed: ${object.sog}kn`}
      >
        <Map
          mapStyle={FREE_MAP_STYLE}
        />
      </DeckGL>

      {/* Map Controls UI */}
      <div className="absolute top-6 right-6 flex flex-col gap-2">
        <div className="bg-slate-950/80 backdrop-blur-md border border-white/10 p-3 rounded-xl text-xs text-white/60">
          <div className="flex items-center gap-2 mb-2">
            <div className="w-2 h-2 rounded-full bg-cyan-400" />
            <span>Normal Operation</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-2 h-2 rounded-full bg-orange-400" />
            <span>Anomaly / Loitering</span>
          </div>
        </div>
      </div>
    </div>
  );
};
