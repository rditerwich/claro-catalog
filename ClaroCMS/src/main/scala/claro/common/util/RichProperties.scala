package claro.common.util

import java.io.{File,FileReader}
import java.util.Properties
import java.net.URI
import claro.common.util.Conversions._
import scala.collection.JavaConversions._

class RichProperties(properties : Properties) {

  def names : Seq[String] = properties.stringPropertyNames.toSeq
  
  def lookup(names : Seq[String]) : Map[String,String] = Map(names map (name => (name,properties.getProperty(name))):_*) 
    
  def findAll(prefix : String) = lookup(names.filter(_.startsWith(prefix)))
  
  def parseAll(prefix : String) : Map[String,String] = Map(names.filter(_.startsWith(prefix)) map (name => (name.substring(prefix.size),properties.getProperty(name))):_*) 
    
  def immutable = lookup(names)

  def apply(name : String) = properties.getProperty(name, "")
  
  def apply(name : String, default : String) = properties.getProperty(name, default)
  
  def isSet(name : String) = properties.getProperty(name) != "false" && properties.getProperty(name) != null
  
  def list(name : String) : List[String] = properties.getProperty(name, "").split(",").toList.trim
  
  /** 
   * Add all properties from other properties that have not been defined by this properties. 
   */
  def merge(other : Properties) = {
	val otherProperties = new RichProperties(other)
    for (name <- otherProperties.names) {
      if (properties.getProperty(name) == null) {
        properties.setProperty(name, otherProperties(name))
      }
    }
  }
  
  def load(uri : URI) : Properties = {
    try {
      val is = uri.open.get
      try {
   		properties.load(is)
      } finally {
    	is.close
      }
    } catch {
      case e =>
    }
    properties
  }
  
  def load(file : File) : Properties = {
    try {
      val reader = new FileReader(file)
      try {
   		properties.load(reader)
      } finally {
    	reader.close
      }
    } catch {
      case e =>
    }
    properties
  }
}
