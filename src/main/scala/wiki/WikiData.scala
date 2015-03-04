package wiki

import java.io.ByteArrayInputStream
import javax.xml.stream.XMLInputFactory

/**
 * Created by barringtonhenry on 9/27/14.
 */
abstract class WikiData(d:String) {
  type DataTup
  val reader = XMLInputFactory.newInstance.createXMLStreamReader(new ByteArrayInputStream(d.getBytes))
  def saveAbstracts(info: DataTup): (WikiAbstractInfo, Boolean)
}
