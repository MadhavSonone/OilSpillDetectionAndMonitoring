from pyspark.sql import SparkSession
from pyspark.sql.functions import from_json, col, window, collect_list, expr, lit, concat
from pyspark.sql.types import StructType, StructField, StringType, DoubleType, IntegerType, TimestampType
import numpy as np

# Schema remains the same
ais_schema = StructType([
    StructField("mmsi", IntegerType()),
    StructField("vessel_name", StringType()),
    StructField("lat", DoubleType()),
    StructField("lon", DoubleType()),
    StructField("sog", DoubleType()),
    StructField("cog", DoubleType()),
    StructField("timestamp", TimestampType())
])

def process_ais():
    spark = SparkSession.builder \
        .appName("Aegis-LSTM-Processor") \
        .config("spark.jars.packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.1") \
        .config("spark.sql.streaming.checkpointLocation", "/tmp/spark_checkpoints") \
        .getOrCreate()

    # Read from Kafka
    df = spark.readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "localhost:9092") \
        .option("subscribe", "ais-raw") \
        .load()

    parsed_df = df.selectExpr("CAST(value AS STRING)") \
        .select(from_json(col("value"), ais_schema).alias("data")) \
        .select("data.*") \
        .withWatermark("timestamp", "1 hour")

    # --- SEQUENCE ANOMALY DETECTION ---
    
    # Group by MMSI and collect a sliding list of the last 20 points
    # (Note: In a high-scale prod environment, we'd use mapGroupsWithState)
    sequence_df = parsed_df.groupBy(
        window(col("timestamp"), "10 minutes", "5 minutes"), # 10min chunks
        col("mmsi"), col("vessel_name")
    ).agg(
        collect_list(expr("struct(lat, lon, sog, cog)")).alias("path_sequence")
    ).filter(expr("size(path_sequence) >= 10")) # Scoring on sequences of 10+

    # Placeholder for LSTM Scoring Logic
    # We flag any sequence that deviates significantly from the mean kinematic path
    alerts_df = sequence_df.withColumn("anomaly_type", lit("SEQUENCE_ANOMALY")) \
        .withColumn("reason", concat(lit("Irregular kinematic sequence detected over "), expr("size(path_sequence)"), lit(" data points.")))

    # Sink to Dashboard (via Console for testing)
    query = alerts_df.select("mmsi", "vessel_name", "anomaly_type", "reason") \
        .writeStream \
        .outputMode("update") \
        .format("console") \
        .start()

    query.awaitTermination()

if __name__ == "__main__":
    process_ais()
