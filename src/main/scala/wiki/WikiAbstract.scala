package wiki

import javax.xml.stream.XMLStreamConstants._
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by barringtonhenry on 9/27/14.
 */
class WikiAbstract(d: String) extends WikiData(d:String)
{

  type DataTup     = (Option[String], Option[String],Option[String])
  val emptyDataRow = (None, None, None)

  def accumTuple(f:() => DataTup, originalVal: DataTup):  DataTup = {
    if(reader.getText.trim.length > 0) {
      f()
    }else{
      originalVal
    }
  }

  def saveAbstracts(info: DataTup): (WikiAbstractInfo, Boolean) = {
    val logger = LoggerFactory.getLogger(this.getClass)

    info match {
      case (Some(name), Some(value), Some(url)) => {
        logger.info("Barry :- This is in the wikiAbstract")
        println("Barry :-" + name + " - " + value + " - " + url)
        ( WikiAbstractInfo(name, value, url), true)
      }

      case _ => (WikiAbstractInfo("", "", ""),false)
    }
  }

  def run(vals:DataTup, ce:String, w: WikiAbstractInfo):WikiAbstractInfo = {
    if(!reader.hasNext) {
      reader.close()
      w
    } else {
      val code: Int = reader.next
      code match {
        case START_ELEMENT => {
          run(vals, reader.getLocalName, w)
        }

        case CHARACTERS => {
          val alu = ce.trim match {
            case "title" =>
              accumTuple( () => {(Option(reader.getText), vals._2, vals._3)}, vals)

            case "abstract"=>
              accumTuple( () => {(vals._1, Option(reader.getText), vals._3)}, vals)

            case "url"=>
              accumTuple( () => {(vals._1, vals._2, Option(reader.getText))}, vals)

            case _  => vals
          }

          val (wikiAbs, isAbs) = saveAbstracts(alu)

          if(isAbs){
            run(emptyDataRow, ce, w)
          }else{
            run(alu, ce, w)
          }

          wikiAbs
        }

        case _ => {
          run(vals, ce, w)
          w
        }
      }
    }
    w
  }
}
