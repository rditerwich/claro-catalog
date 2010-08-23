package claro.cms.webshop.data

import claro.cms.Cms
import claro.jpa.catalog._
import scala.collection.JavaConversions._

trait WebshopDataFiller {
  val em = Cms.entityManager

  def property(item : Item, name : String, tpe : PropertyType) : Property = {
    for (property <- item.getProperties) {
      for (label <- property.getLabels) {
        if (label.getLabel == name && label.getLanguage == null) {
          property.setType(tpe)
          return property
        }
      }
    }
    val property = new Property 
    label(property, name)
    property.setType(tpe)
    property
  }
  
  def set(item : Item, property : Property, value : Any, language : String = null) = {
    val value = item.getPropertyValues.find(v => v.getProperty == property && v.getLanguage == language).getOrElse(new PropertyValue)
    value.setProperty(property)
    value.setLanguage(language)
    property.getType match {
      case PropertyType.String => value.setStringValue(value.asInstanceOf[String])
      case PropertyType.Money => value.setMoneyValue(value.asInstanceOf[Double])
    }
  }
  
  def label(property : Property, name : String, language : String = null) = {
    val label = property.getLabels.find(l => l.getProperty == property && l.getLanguage == language) getOrElse new Label
    label.setLanguage(language)
    label.setLabel(name)
    label.setProperty(property)
    property.getLabels.add(label)
    label
  }

}