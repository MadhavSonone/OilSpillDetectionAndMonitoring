from sqlalchemy import Column, Integer, String, Float, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base
from geoalchemy2 import Geometry
import datetime

Base = declarative_base()

class Vessel(Base):
    __tablename__ = "vessels"
    
    mmsi = Column(Integer, primary_key=True, index=True)
    name = Column(String, index=True)
    vessel_type = Column(String)
    flag = Column(String)
    last_updated = Column(DateTime, default=datetime.datetime.utcnow)

    trajectories = relationship("Trajectory", back_populates="vessel")

class Trajectory(Base):
    __tablename__ = "trajectories"
    
    id = Column(Integer, primary_key=True, index=True)
    vessel_mmsi = Column(Integer, ForeignKey("vessels.mmsi"))
    timestamp = Column(DateTime, index=True)
    location = Column(Geometry(geometry_type='POINT', srid=4326))
    sog = Column(Float)  # Speed Over Ground
    cog = Column(Float)  # Course Over Ground
    heading = Column(Float)
    
    vessel = relationship("Vessel", back_populates="trajectories")

class OilSpill(Base):
    __tablename__ = "oil_spills"
    
    id = Column(Integer, primary_key=True, index=True)
    timestamp = Column(DateTime, default=datetime.datetime.utcnow)
    boundary = Column(Geometry(geometry_type='POLYGON', srid=4326))
    confidence = Column(Float)
    area_sq_km = Column(Float)
    image_source = Column(String)

class Alert(Base):
    __tablename__ = "alerts"
    
    id = Column(Integer, primary_key=True, index=True)
    timestamp = Column(DateTime, default=datetime.datetime.utcnow)
    alert_type = Column(String)  # e.g., "ANOMALY", "SPILL", "ZONE_BREACH"
    severity = Column(String)    # e.g., "INFO", "WARNING", "CRITICAL"
    message = Column(String)
    vessel_mmsi = Column(Integer, ForeignKey("vessels.mmsi"), nullable=True)
    location = Column(Geometry(geometry_type='POINT', srid=4326), nullable=True)
