package claro.common.util

import java.io.InputStream
import xml.{NodeSeq,XML,Text}
import xml.parsing.{NoBindingFactoryAdapter}
import org.xml.sax.{SAXParseException,InputSource}

/**
 * Loads XHTML data and returns parse errors as an html structure embedded in the
 * result.
 */
object ParseHtml {
  
  object NoInput extends Exception
  
  def apply(input : => InputStream, publicId : String) : (NodeSeq,Option[Throwable]) = {
    input match {
      case null => (NodeSeq.Empty,Some(NoInput))
      case is =>
        try {
          val source = new InputSource(is)
          source.setPublicId(publicId)
          val adapter = new NoBindingFactoryAdapter() {
              override def printError(errtype: String, ex: SAXParseException) = {}
          }
          (adapter.loadXML(source),None)
        } catch {
          case e : SAXParseException =>
            val is2 = input
            try {
            (mkError(io.Source.fromInputStream(is2), e, 0),Some(e))
          } finally {
            is2.close
          }
          case e  => (Text("Error in " + publicId + ": " + e.getMessage),Some(e))
        } finally {
          is.close
        }
    }
  }
  
  def apply(input : String) : NodeSeq = {
    try {
     XML.loadString("<root>\n" + input + "\n</root>").child
    } catch {
      case e : SAXParseException => mkError(io.Source.fromString(input), e, 1) 
      case e : Throwable => Text(e.getMessage)
    }
  }
  
  def mkError(input : io.Source, e : SAXParseException, corr : Int) : NodeSeq = {
		  val lines = Array(input.getLines.toList:_*)
		  val index = e.getLineNumber - 1 - corr
		  if (index < lines.size) {
			  <span class="xml-parse-error">
			  <span>{e.getMessage}</span>
			  <pre>{for (line <- lines.take(index)) yield line
			  }<b>{lines(index)}</b>{
				  for (line <- lines.drop(index + 1)) yield line}</pre></span>
		  } else {
			  <span class="xml-parse-error">
			  <span>{e.getMessage}</span>
			  <pre>{lines}</pre></span>
		  } 
  }
}
