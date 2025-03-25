from pyspark.sql import SparkSession
from pyspark.sql.functions import max as spark_max

def run_latest_ais_job():
    print("🚀 Starting latest AIS data refresh...")

    # Initialize SparkSession
    spark = SparkSession.builder \
        .appName("LatestAISDataJob") \
        .master("spark://spark-master:7077") \
        .config("spark.cassandra.connection.host", "cassandra") \
        .config("spark.cassandra.connection.port", "9042") \
        .getOrCreate()

    # Read full AIS data from Cassandra
    ais_df = spark.read \
        .format("org.apache.spark.sql.cassandra") \
        .options(table="aisdata", keyspace="ais") \
        .load()

    print("✅ Loaded AIS data with schema:")
    ais_df.printSchema()

    # Group by mmsi to get latest timestamp and join back
    latest_df = ais_df.groupBy("mmsi") \
        .agg(spark_max("timestamp").alias("timestamp")) \
        .join(ais_df, on=["mmsi", "timestamp"])

    # Select only needed columns – ensure these exist in ais_df schema
    columns_to_keep = ["mmsi", "timestamp", "latitude", "longitude", "vessel_name"]
    latest_df = latest_df.select(columns_to_keep)

    # Save to Cassandra with overwrite + truncate confirmation
    latest_df.write \
        .format("org.apache.spark.sql.cassandra") \
        .options(table="latest_ais_data", keyspace="ais") \
        .option("confirm.truncate", True) \
        .mode("overwrite") \
        .save()

    print("✅ Refreshed latest_ais_data table.")

    spark.stop()
    print("🛑 Spark session stopped.")

# Run job once (remove schedule if you're doing it manually)
run_latest_ais_job()
