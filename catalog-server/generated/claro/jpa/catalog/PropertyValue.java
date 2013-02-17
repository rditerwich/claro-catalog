package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Double;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import claro.jpa.media.MediaContent;

@SuppressWarnings("serial")
public class PropertyValue implements Serializable {
    private Long id;
    private Property property;
    private String language;
    private StagingArea stagingArea;
    private OutputChannel outputChannel;
    private Source source;
    private String stringValue;
    private Integer integerValue;
    private Integer enumValue;
    private Double realValue;
    private Boolean booleanValue;
    private Double moneyValue;
    private String moneyCurrency;
    private MediaContent mediaValue;
    private Item itemValue;
    private Item item;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property value) {
        this.property = value;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String value) {
        this.language = value;
    }

    public StagingArea getStagingArea() {
        return stagingArea;
    }

    public void setStagingArea(StagingArea value) {
        this.stagingArea = value;
    }

    public OutputChannel getOutputChannel() {
        return outputChannel;
    }

    public void setOutputChannel(OutputChannel value) {
        this.outputChannel = value;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source value) {
        this.source = value;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String value) {
        this.stringValue = value;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer value) {
        this.integerValue = value;
    }

    public Integer getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(Integer value) {
        this.enumValue = value;
    }

    public Double getRealValue() {
        return realValue;
    }

    public void setRealValue(Double value) {
        this.realValue = value;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean value) {
        this.booleanValue = value;
    }

    public Double getMoneyValue() {
        return moneyValue;
    }

    public void setMoneyValue(Double value) {
        this.moneyValue = value;
    }

    public String getMoneyCurrency() {
        return moneyCurrency;
    }

    public void setMoneyCurrency(String value) {
        this.moneyCurrency = value;
    }

    public MediaContent getMediaValue() {
        return mediaValue;
    }

    public void setMediaValue(MediaContent value) {
        this.mediaValue = value;
    }

    public Item getItemValue() {
        return itemValue;
    }

    public void setItemValue(Item value) {
        this.itemValue = value;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item value) {
        this.item = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PropertyValue) {
            PropertyValue otherPropertyValue = (PropertyValue) other;
            if (this.id == null) {
                if (otherPropertyValue.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherPropertyValue.id)) {
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (id  == null? 0 : id .hashCode());
        return result;
    }

}