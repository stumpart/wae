/**
 * Created by barringtonhenry on 9/9/14.
 */



import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.hadoop.conf.Configuration
import bigxml.XmlInputFormat
import org.apache.spark.scheduler.InputFormatInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import wiki._


object LascoScript {

  /*type DataTup     = (Option[String], Option[String],Option[String])
  val emptyDataRow = (None, None, None)*/

  def urlses(cl: ClassLoader): Array[java.net.URL] = cl match {
    case null => Array()
    case u: java.net.URLClassLoader => u.getURLs() ++ urlses(cl.getParent)
    case _ => urlses(cl.getParent)
  }

  val  urls = urlses(getClass.getClassLoader)


  def main(arg: Array[String]): Unit ={
    val hadoopConf = new Configuration()
    val input      = "input"

    val locData =    InputFormatInfo.computePreferredLocations(
      Seq(new InputFormatInfo(hadoopConf, classOf[XmlInputFormat], "hdfs://barrymac:9000/user/barringtonhenry/input")
    ))

    val conf = new SparkConf().set("spark.driver.host", "barrymac")
                              .setMaster("yarn-client")
                              .setAppName("Lasco Script")
    conf.setJars(List("/Users/barringtonhenry/Documents/scala_workspace/LascoScript/lib/xmlinput.jar") ++ SparkContext.jarOfClass(this.getClass).toList)

    val sc   = new SparkContext(conf, locData)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val logger = LoggerFactory.getLogger(this.getClass)
    logger.info("Barry :- This is running scala")

    hadoopConf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", " ")
    hadoopConf.set("xmlinput.start", "<doc>")
    hadoopConf.set("xmlinput.end", "</doc>")
    hadoopConf.set("fs.default.name", "hdfs://barrymac:9000")

    val wikiRdd = sc.newAPIHadoopFile(input, classOf[XmlInputFormat],
      classOf[org.apache.hadoop.io.LongWritable],
      classOf[org.apache.hadoop.io.Text],
      hadoopConf
    )

    WikiAbstractInfo("", "", "")

    val paraRdd = wikiRdd.map {
      case (lg, bytes) => {
        import wiki._
        val f = new wiki.WikiAbstract(bytes.toString).run((None, None, None), "", wiki.WikiAbstractInfo("", "", ""))
        (lg.toString.toLong, bytes.toString())
        f
      }
    }.collect().take(10)

    //sqlContext.createParquetFile("hdfs://barrymac:9000/user/barringtonhenry/output/wiki.parquet", true).registerTempTable("")


    paraRdd.foreach { println }
  }
}
