package claro.jpa.catalog;

import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.StagingArea;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Sat Dec 18 13:29:15 CET 2010")
@StaticMetamodel(PropertyValue.class)
public class PropertyValue_ { 

    public static volatile SingularAttribute<PropertyValue, OutputChannel> outputChannel;
    public static volatile SingularAttribute<PropertyValue, String> stringValue;
    public static volatile SingularAttribute<PropertyValue, Property> property;
    public static volatile SingularAttribute<PropertyValue, Double> moneyValue;
    public static volatile SingularAttribute<PropertyValue, Boolean> booleanValue;
    public static volatile SingularAttribute<PropertyValue, Long> id;
    public static volatile SingularAttribute<PropertyValue, Item> itemValue;
    public static volatile SingularAttribute<PropertyValue, Item> item;
    public static volatile SingularAttribute<PropertyValue, Integer> integerValue;
    public static volatile SingularAttribute<PropertyValue, String> language;
    public static volatile SingularAttribute<PropertyValue, Integer> enumValue;
    public static volatile SingularAttribute<PropertyValue, byte[]> mediaValue;
    public static volatile SingularAttribute<PropertyValue, StagingArea> stagingArea;
    public static volatile SingularAttribute<PropertyValue, ImportSource> importSource;
    public static volatile SingularAttribute<PropertyValue, String> moneyCurrency;
    public static volatile SingularAttribute<PropertyValue, String> mimeType;
    public static volatile SingularAttribute<PropertyValue, Double> realValue;

}