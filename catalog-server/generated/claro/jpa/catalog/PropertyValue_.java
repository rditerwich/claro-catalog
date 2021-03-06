package claro.jpa.catalog;

import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.Source;
import claro.jpa.catalog.StagingArea;
import claro.jpa.media.MediaContent;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2013-02-22T11:24:28")
@StaticMetamodel(PropertyValue.class)
public class PropertyValue_ { 

    public static volatile SingularAttribute<PropertyValue, OutputChannel> outputChannel;
    public static volatile SingularAttribute<PropertyValue, String> stringValue;
    public static volatile SingularAttribute<PropertyValue, Property> property;
    public static volatile SingularAttribute<PropertyValue, Double> moneyValue;
    public static volatile SingularAttribute<PropertyValue, Boolean> booleanValue;
    public static volatile SingularAttribute<PropertyValue, Long> id;
    public static volatile SingularAttribute<PropertyValue, Source> source;
    public static volatile SingularAttribute<PropertyValue, Item> itemValue;
    public static volatile SingularAttribute<PropertyValue, Item> item;
    public static volatile SingularAttribute<PropertyValue, Integer> integerValue;
    public static volatile SingularAttribute<PropertyValue, String> language;
    public static volatile SingularAttribute<PropertyValue, MediaContent> mediaValue;
    public static volatile SingularAttribute<PropertyValue, Integer> enumValue;
    public static volatile SingularAttribute<PropertyValue, StagingArea> stagingArea;
    public static volatile SingularAttribute<PropertyValue, String> moneyCurrency;
    public static volatile SingularAttribute<PropertyValue, Double> realValue;

}