from pyspark.sql import SparkSession
from pyspark.sql.functions import col, to_timestamp

import os
print(os.getcwd())


# Initialize Spark with Cassandra connector
spark = SparkSession.builder \
    .appName("AIS Loader") \
    .config("spark.cassandra.connection.host", "cassandra") \
    .config("spark.cassandra.connection.port", "9042") \
    .getOrCreate()



# Read CSV
df = spark.read.csv("/opt/bitnami/spark/data.csv", header=True)

from pyspark.sql.functions import col, to_timestamp

selected_df = df.select(
    col("MMSI").alias("mmsi"),
    col("LAT").cast("double").alias("latitude"),
    col("LON").cast("double").alias("longitude"),
    col("SOG").cast("double").alias("sog"),
    col("COG").cast("double").alias("cog"),
    col("Heading").cast("double").alias("heading"),
    to_timestamp("BaseDateTime", "yyyy-MM-dd'T'HH:mm:ss").alias("timestamp"),
    col("VesselName").alias("vessel_name")
)


# Write to Cassandra
selected_df.write \
    .format("org.apache.spark.sql.cassandra") \
    .options(table="aisdata", keyspace="ais") \
    .mode("append") \
    .save()

print("Data loaded to Cassandra successfully!")

spark.stop()
