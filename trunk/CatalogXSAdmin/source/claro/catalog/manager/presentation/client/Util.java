package claro.catalog.manager.presentation.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import claro.catalog.manager.presentation.client.binding.Binding;
import claro.catalog.manager.presentation.client.binding.BindingConverters;
import claro.catalog.manager.presentation.client.binding.CheckBoxBinding;
import claro.catalog.manager.presentation.client.binding.ListPropertyBinding;
import claro.catalog.manager.presentation.client.binding.TextBoxBaseBinding;
import claro.catalog.manager.presentation.client.cache.CatalogCache;
import claro.catalog.manager.presentation.client.catalog.Category;
import claro.catalog.manager.presentation.client.catalog.EnumValue;
import claro.catalog.manager.presentation.client.catalog.Item;
import claro.catalog.manager.presentation.client.catalog.Label;
import claro.catalog.manager.presentation.client.catalog.Language;
import claro.catalog.manager.presentation.client.catalog.ParentChild;
import claro.catalog.manager.presentation.client.catalog.Product;
import claro.catalog.manager.presentation.client.catalog.Property;
import claro.catalog.manager.presentation.client.catalog.PropertyType;
import claro.catalog.manager.presentation.client.catalog.PropertyValue;
import claro.catalog.manager.presentation.client.catalog.PropertyValueBinding;
import claro.catalog.manager.presentation.client.i18n.I18NCatalogXS;
import claro.catalog.manager.presentation.client.shop.Navigation;
import claro.catalog.manager.presentation.client.widget.EnumValuesEditWidget;
import claro.catalog.manager.presentation.client.widget.MediaWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;


public class Util {
	public final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

	public static final String NAME = "Name";
	public static final String ARTICLENUMBER= "ArticleNumber";
	public static final String PRICE = "Price";
	public static final String DESCRIPTION = "Description";
	public static final String IMAGE = "Image";

	
	public static void add(UIObject uiObject, Styles style) {
		uiObject.addStyleName(style.toString());
	}

	public static interface AddHandler<T> {
		void onAdd(T data);
	}

	public static interface DeleteHandler<T> {
		void onDelete(T data);
	}

	public static class ListPropertyTypeBinding extends
			ListPropertyBinding<PropertyType> {
		private static final List<PropertyType> PROPERTY_TYPE_LIST = new ArrayList<PropertyType>();

		static {
			for (PropertyType i : PropertyType.values()) {
				PROPERTY_TYPE_LIST.add(i);
			}
		}

		public PropertyType get(int index) {
			return PROPERTY_TYPE_LIST.get(index);
		}

		@Override
		protected List<PropertyType> doGetData() {
			return PROPERTY_TYPE_LIST;
		}

		@Override
		protected void doSetData(List<PropertyType> data) {
			// Nothing to do, list doesn't change.
		}

		/**
		 * Initializes the data set, call this method AFTER the instance is
		 * bound.
		 */
		public void set() {
			setData(PROPERTY_TYPE_LIST);
		}
	}

	// private static final Label EMPTY_LABEL = new Label();

	// static {
	// EMPTY_LABEL.setLabel("UNKNOWN LABEL");
	// }

	public static Set<Category> categories(List<Navigation> navigations) {
		Set<Category> result = new HashSet<Category>();

		categories(navigations, result);

		return result;
	}

	public static void categories(List<Navigation> navigations,
			Set<Category> result) {
		for (Navigation navigation : navigations) {
			result.add(navigation.getCategory());
			categories(navigation.getSubNavigation(), result);
		}
	}

	/**
	 * Returns true if the matching languages are both null or if they have the
	 * same value.
	 * 
	 * @param lang
	 * @param pvLang
	 * @return
	 */
	public static boolean matchLang(String lang, String pvLang) {
		return (lang == null && pvLang == null)
				|| (lang != null && lang.equals(pvLang));
	}

	/**
	 * Returns a list of all PropertyValues based on all properties in a
	 * Category. If no propertyValue is present, a new one is created. The newly
	 * created propertyValues are added to the current productGroup.
	 * 
	 * @param pg
	 * @param values
	 * @return
	 */
	public static List<PropertyValue[]> getCategoryPropertyValues(List<Language> langs, Category pg, Item item) {
		final List<PropertyValue> values = item.getPropertyValues();
		final List<PropertyValue[]> pgValues = new ArrayList<PropertyValue[]>();
		final ArrayList<Language> allLangs = new ArrayList<Language>(langs);

		if (!langs.contains(null)) {
			allLangs.add(null);
		}
		for (Property property : pg.getProperties()) {
			if (property.getItem() == null) {
				property.setItem(pg);
			}
			final PropertyValue[] pgValue = new PropertyValue[allLangs.size()];

			pgValues.add(pgValue);
			for (int i = 0; i < allLangs.size(); i++) {
				final Language lLang = allLangs.get(i);
				final String lang = lLang != null ? lLang.getName() : null;

				PropertyValue found = null;
				for (PropertyValue value : values) {
					if (value.getProperty().getId().equals(property.getId())
							&& ((lang == null && value.getLanguage() == null) || lang != null
									&& lang.equals(value.getLanguage()))) {
						found = value;
						break;
					}
				}
				if (found == null) {
					found = new PropertyValue();
					found.setProperty(property);
					found.setLanguage(lang);
					found.setItem(item);
					item.getPropertyValues().add(found);
				}
				pgValue[i] = found;
				/*
				 * boolean foundLabel = false; for (Label pl :
				 * property.getLabels()) { if ((lang == null && pl.getLanguage()
				 * == null) || (lang !=null && lang.equals(pl.getLanguage()))) {
				 * foundLabel = true; break; } } if (!foundLabel) { final Label
				 * l = new Label(); l.setLanguage(lang);
				 * property.getLabels().add(l); }
				 */
			}
		}
		return pgValues;
	}

	public static Property getPropertyByName(List<Property> properties,
			String name, String lang) {
		for (Property p : properties) {
			for (Label label : p.getLabels()) {
				if (name.equals(label.getLabel())
						&& ((lang == null && label.getLanguage() == null) || (lang != null && lang
								.equals(label.getLanguage())))) {
					return p;
				}
			}
		}
		return null;
	}

	public static PropertyValue getPropertyValueByLangByName(
			List<PropertyValue[]> values, String name, String lang) {
		for (PropertyValue[] propertyValues : values) {
			for (PropertyValue propertyValue : propertyValues) {
				if (name.equals(getLabel(propertyValue, lang, true).getLabel())) {
					return propertyValue;
				}
			}
		}
		return null;
	}
	
	public static String name(Item item, String lang) {
		PropertyValue nameProperty = getNameProperty(item, lang);
		if (nameProperty == null) {
			return "";
		}
		String stringValue = nameProperty.getStringValue();
		if (stringValue == null && lang != null) {
			stringValue = name(item, null);
		}
		return stringValue;
	}
	
	public static PropertyValue getNameProperty(Item item, String lang) {
		return getPropertyValueByName(item, NAME, lang);
	}
	
	public static String articleNumber(Item item, String lang) {
		PropertyValue articleNumberProperty = getArticleNumberProperty(item, lang);
		if (articleNumberProperty == null) {
			return "";
		}
		String stringValue = articleNumberProperty.getStringValue();
		if (stringValue == null && lang != null) {
			stringValue = name(item, null);
		}
		return stringValue;
	}
	
	public static PropertyValue getArticleNumberProperty(Item item, String lang) {
		return getPropertyValueByName(item, ARTICLENUMBER, lang);
	}
	
	public static String price(Item item) {
		PropertyValue priceProperty = getPropertyValueByName(item, PRICE, null);
		if (priceProperty == null) {
			return "";
		}
		Double value = priceProperty.getMoneyValue();
		String moneyCurrency = priceProperty.getMoneyCurrency();
		if (value != null) {
			if (moneyCurrency == null) {
				moneyCurrency = "EUR";
			}
			return NumberFormat.getCurrencyFormat(moneyCurrency).format(value);
		}
		return "";
	}
	
	public static String description(Item item, String lang) {
		PropertyValue descriptionProperty = getDescriptionProperty(item, lang);
		if (descriptionProperty == null) {
			return "";
		}
		String stringValue = descriptionProperty.getStringValue();
		if (stringValue == null && lang != null) {
			stringValue = description(item, null);
		}
		return stringValue;
	}
	
	public static PropertyValue getDescriptionProperty(Item item, String lang) {
		return getPropertyValueByName(item, DESCRIPTION, lang);
	}
	
	
	public static PropertyValue imagePropertyValue(Item item) {
		PropertyValue result = getPropertyValueByName(item, IMAGE, null);
		if (result != null && result.getProperty().getType() == PropertyType.Media) {
			return result;
		}
		return null;
	}
	
	public static PropertyValue getPropertyValueByName(Item item, String name, String lang) {
		if (item == null) {
			return null;
		}
		return getPropertyValueByName(item.getPropertyValues(), name, lang);
	}
	
	/**
	 * Returns the PropertyValue with a Property with the name in the given
	 * language or if no Property for that language for the null language.
	 * 
	 * @param values
	 *            List of PropertyValues to look through.
	 * @param name
	 *            Name of the Property
	 * @param lang
	 *            Language to select
	 * @return
	 */
	public static PropertyValue getPropertyValueByName(List<PropertyValue> values, String name, String lang) {
		PropertyValue dpv = null;
		PropertyValue pv = null;

		for (PropertyValue propertyValue : values) {
			if (name.equals(getLabel(propertyValue, lang, true).getLabel())) {
				if (propertyValue.getLanguage() == null) {
					dpv = propertyValue;
				} else if (lang != null && lang.equals(propertyValue.getLanguage())) {
					pv = propertyValue;
				}
			}
		}
		return pv == null ? dpv : pv;
	}

	public static Label getLabel(PropertyValue value, String lang) {
		return value == null ? null : getLabel(value.getProperty().getLabels(),
				lang);
	}

	public static Label getLabel(PropertyValue value, String lang,
			boolean fallback) {
		return value == null ? null : getLabel(value.getProperty().getLabels(),
				lang, fallback);
	}

	public static Label getLabel(List<Label> labels, String lang) {
		return getLabel(labels, lang, false);
	}

	/**
	 * Returns the label matching the language. If lang is not null, but the
	 * list of Labels contains a label with language null this label is returns.
	 * If no match could be found, an empty label is returned.
	 * 
	 * @param labels
	 * @param lang
	 * @return
	 */
	public static Label getLabel(List<Label> labels, String lang,
			boolean fallback) {
		if (labels != null) {
			for (Label label : labels) {
				if ((lang == null && label.getLanguage() == null)
						|| (lang != null && lang.equals(label.getLanguage()) && (!fallback || (label
								.getLabel() != null && !"".equals(label
								.getLabel()))))) {
					return label;
				}
			}
			if (fallback) {
				for (Label label : labels) {
					if (label.getLanguage() == null) {
						return label;
					}
				}
			}
		}
		final Label lbl = new Label();
		lbl.setLanguage(lang);

		return lbl;
	}

	public static List<Property> filterEmpty(List<Property> properties) {
		for (Property property : properties) {
			final List<Label> nl = new ArrayList<Label>();

			for (Label lbl : property.getLabels()) {
				if (lbl.getLabel() != null || !"".equals(lbl.getLabel())) {
					nl.add(lbl);
				}
			}
			property.setLabels(nl);
		}
		return properties;
	}

	public static List<PropertyValue> filterEmpty(
			Collection<PropertyValue> values) {
		final List<PropertyValue> nv = new ArrayList<PropertyValue>();

		for (PropertyValue pv : values) {
			if (!isEmpty(pv)) {
				nv.add(pv);
			}
		}
		return nv;
	}

	/**
	 * Returns true if all PropertyValue data fields all null.
	 * 
	 * @param pv
	 *            PropertyValue
	 * @return
	 */
	public static boolean isEmpty(PropertyValue pv) {
		return pv == null
				|| ((pv.getStringValue() == null || "".equals(pv
						.getStringValue()))
						&& pv.getIntegerValue() == null
						&& pv.getEnumValue() == null
						&& pv.getRealValue() == null
						&& pv.getBooleanValue() == null
						&& (pv.getMoneyValue() == null || "".equals(pv
								.getMoneyValue()))
						&& pv.getMoneyCurrency() == null
						&& pv.getMediaValue() == null && pv.getMimeType() == null);
	}

	/**
	 * Returns all ID's of the parents of the given Category.
	 * 
	 * @param productGroup
	 * @return
	 */
	public static List<Long> findParents(Category productGroup) {
		final List<Long> parents = new ArrayList<Long>();

		findParents(productGroup, parents);
		return parents;
	}

	private static void findParents(Item productGroup, List<Long> parents) {
		if (productGroup != null && productGroup.getParents() != null) {
			for (ParentChild parent : productGroup.getParents()) {
				if (!parents.contains(parent.getParent().getId())) {
					findParents(parent.getParent(), parents);
					parents.add(parent.getParent().getId());
				}
			}
		}
	}

	public static Binding bindPropertyValue(PropertyType pt, Widget w,
			PropertyValueBinding pvb) {
		Binding value = null;
		switch (pt) {
		case Enum:
			value = ((EnumValuesEditWidget) w).bind(pvb);
			break;
		case FormattedText:
			value = TextBoxBaseBinding.bind((TextBoxBase) w, pvb.stringValue(),
					BindingConverters.STRING_CONVERTER);
			break;
		case Media:
			value = ((MediaWidget) w).bind(pvb);
			break;
		case String:
			value = TextBoxBaseBinding.bind((TextBoxBase) w, pvb.stringValue(),
					BindingConverters.STRING_CONVERTER);
			break;
		case Boolean:
			value = CheckBoxBinding.bind((CheckBox) w, pvb.booleanValue());
			break;
		case Real:
			value = TextBoxBaseBinding.bind((TextBoxBase) w, pvb.realValue(),
					BindingConverters.DOUBLE_CONVERTER);
			break;
		case Money:
			value = TextBoxBaseBinding.bind((TextBoxBase) w, pvb.moneyValue(),
					BindingConverters.DOUBLE_CONVERTER);
			break;
		case Acceleration:
		case AmountOfSubstance:
		case Angle:
		case Area:
		case ElectricCurrent:
		case Energy:
		case Frequency:
		case Integer:
		case Length:
		case LuminousIntensity:
		case Power:
		case Time:
		case Mass:
		case Temperature:
		case Velocity:
		case Voltage:
		case Volume:
		default:
			value = TextBoxBaseBinding.bind((TextBoxBase) w,
					pvb.integerValue(), BindingConverters.INTEGER_CONVERTER);
		}
		return value;
	}

	/**
	 * Returns a string with all the generic Product properties appended.
	 * 
	 * @param product
	 * @param language
	 * @return
	 */
	public static String productToString(Product product, String language) {
		final List<PropertyValue[]> pvs = Util.getCategoryPropertyValues(
				CatalogCache.get().getCurrentCatalog().getLanguages(),
				CatalogCache.get().getCategoryProduct(), product);
		final StringBuffer s = new StringBuffer();

		for (PropertyValue[] pvhlangs : pvs) {
			PropertyValue dpv = null;
			PropertyValue lpv = null;
			for (PropertyValue propv : pvhlangs) {
				if (language != null && language.equals(propv.getLanguage())) {
					lpv = propv;
				} else if (propv.getLanguage() == null) {
					dpv = propv;
				}
			}
			final String v = Util.propertyValueToString(
					lpv != null && !Util.isEmpty(lpv) ? lpv : dpv, language);

			s.append(v);
			if (!"".equals(v)) {
				s.append(" ");
			}
		}
		return s.toString();
	}

	/**
	 * Converts a propertyValue to String.
	 * 
	 * @param pv
	 * @param language
	 * @return
	 */
	public static String propertyValueToString(PropertyValue pv, String language) {
		if (pv == null)
			return "";
		Object value = null;

		switch (pv.getProperty().getType()) {
		case Enum:
			for (EnumValue enumValue : pv.getProperty().getEnumValues()) {
				if (enumValue.getId().equals(pv.getEnumValue())) {
					value = getLabel(
							pv.getProperty().getEnumValues()
									.get(pv.getEnumValue()).getLabels(),
							language, true);
					break;
				}
			}
			break;
		case FormattedText:
			value = pv.getStringValue();
			break;
		case Media:
			value = "";// pv.getStringValue();
			break;
		case String:
			value = pv.getStringValue();
			break;
		case Boolean:
			value = pv.getBooleanValue();
			break;
		case Real:
			value = pv.getRealValue();
			break;
		case Money:
			value = Util.formatMoney(pv.getMoneyValue());
			break;
		case Acceleration:
		case AmountOfSubstance:
		case Angle:
		case Area:
		case ElectricCurrent:
		case Energy:
		case Frequency:
		case Integer:
		case Length:
		case LuminousIntensity:
		case Power:
		case Time:
		case Mass:
		case Temperature:
		case Velocity:
		case Voltage:
		case Volume:
			value = pv.getIntegerValue();
		default:
			value = pv.getStringValue();
		}
		return Util.stringValueOf(value);
	}

	/**
	 * Formats money to a euro format.
	 * 
	 * @param money
	 * @return
	 */
	public static String formatMoney(Double money) {
		return money == null ? "" : NumberFormat.getCurrencyFormat("EUR").format(money);
	}

	/**
	 * Converts the objectValue to a String and if objectValue == null return an
	 * empty String.
	 * 
	 * @param objectValue
	 * @return
	 */
	public static String stringValueOf(Object objectValue) {
		return objectValue == null ? "" : String.valueOf(objectValue);
	}
}
