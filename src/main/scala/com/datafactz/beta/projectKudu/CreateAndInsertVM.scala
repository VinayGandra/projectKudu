package com.datafactz.beta.projectKudu

import org.apache.kudu.client.CreateTableOptions
import org.apache.kudu.spark.kudu.KuduContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types.{DecimalType, DoubleType, StructType}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters._

object CreateAndInsertVM {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Kudu DDL")
    val sc = new SparkContext(conf)
    val sqlContext = new HiveContext(sc)

    val kuduMaster = "quickstart.cloudera:7051"

    val kuduContext = new KuduContext(kuduMaster)

    // Later get all tables in hive db in a list and perform ddl
    val hiveTableName = "tpcds_parquet.item"
    val kuduTableName = "sp_item"

    // Choose which option
    if (kuduContext.tableExists(kuduTableName)) {
      kuduContext.deleteTable(kuduTableName)
    }

    val hiveDF = sqlContext.table(hiveTableName)
    var hiveTableSchema = hiveDF.schema.fields

    // Change un-supported kudu datatypes not handled by KuduContext
    hiveTableSchema.indices.foreach(x => if(hiveTableSchema(x).
      dataType == DecimalType(7,2)){hiveTableSchema(x) = hiveTableSchema(x).copy(dataType=DoubleType)})

    val kuduTableSchema = StructType(hiveTableSchema)

    val kuduPrimaryKey = Seq("i_item_sk")

    val kuduTableOptions = new CreateTableOptions()

    // Minimum two buckets for hash partitioning
    kuduTableOptions.addHashPartitions(kuduPrimaryKey.asJava, 2).
      setNumReplicas(1)

    // Create kudu table
    kuduContext.createTable(kuduTableName, kuduTableSchema, kuduPrimaryKey, kuduTableOptions)

    // Insert data into kudu table
    kuduContext.insertRows(hiveDF, kuduTableName)
  }
}
