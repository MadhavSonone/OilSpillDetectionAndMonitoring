from pyspark.sql import SparkSession

spark = SparkSession.builder \
    .appName("AISDataCount") \
    .config("spark.cassandra.connection.host", "cassandra") \
    .config("spark.cassandra.connection.port", "9042") \
    .getOrCreate()

df = spark.read \
    .format("org.apache.spark.sql.cassandra") \
    .options(table="aisdata", keyspace="ais") \
    .load()

print("Total rows:", df.count())
