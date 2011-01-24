package claro.catalog.impl.items;

import java.util.Comparator;
import java.util.List;

import com.google.common.base.Objects;

import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.util.Money;

public class ItemOrderComparator implements Comparator<ItemModel>{
	private final List<Property> orderBy;
	private final String language;
	private StagingArea stagingArea;
	private OutputChannel outputChannel;

	public ItemOrderComparator(List<Property> orderBy, StagingArea stagingArea, OutputChannel outputChannel, String language) {
		this.orderBy = orderBy;
		this.language = language;
		validate(orderBy);
	}

	@Override
	public int compare(ItemModel o1, ItemModel o2) {
		for (Property p : orderBy) {
			PropertyModel o1Property = o1.findProperty(p, true);
			PropertyModel o2Property = o2.findProperty(p, true);
			if (o1Property == null || o2Property == null) {
				throw new IllegalStateException("Not all items have property with id " + p.getId());  // TODO better solution?
			}

			Object value1 = o1Property.getEffectiveValues(stagingArea, outputChannel).tryGet(language, null);
			Object value2 = o2Property.getEffectiveValues(stagingArea, outputChannel).tryGet(language, null);

			return compare(value1, value2, p.getType());
		}
		return 0;
	}

	private int compare(Object value1, Object value2, PropertyType type) {
		Integer result = compareNulls(value1, value2);
		if (result != null) {
			return result;
		}
		switch (type) {
		case Money: 
			Money moneyValue1 = (Money)value1;
			Money moneyValue2 = (Money)value2;

			result = compareNulls(moneyValue1.value, moneyValue2.value);
			if (result != null) {
				return result;
			}
			
			if (moneyValue1.value < moneyValue2.value) {
				return -1;
			}
			if (moneyValue1.value > moneyValue2.value) {
				return 1;
			}
			return 0;
			// TODO Add more types.
		default:
			String stringValue1 = (String) value1;
			String stringValue2 = (String) value2;
			return stringValue1.compareTo(stringValue2);
		}
	}

	private Integer compareNulls(Object value1, Object value2) {
		if (value1 == value2) {
			return 0;
		}
		if (value1 == null && value2 != null) {
			return -1;
		}
		if (value1 != null && value2 == null) {
			return 1;
		}
		
		return null;
	}
	
	private void validate(List<Property> orderBy2) {
		for (Property p : orderBy2) {
			switch (p.getType()) {
			case Media: throw new IllegalArgumentException("Cannot order on media property");
			}
		}
	}
	
	
	
}
