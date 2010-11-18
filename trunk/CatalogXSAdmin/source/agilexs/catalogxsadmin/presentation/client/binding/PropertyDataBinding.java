package agilexs.catalogxsadmin.presentation.client.binding;

import java.util.List;
import java.util.Map;

import claro.catalog.model.PropertyData;
import claro.jpa.catalog.PropertyValue;

public class PropertyDataBinding extends ObjectBinding<PropertyData> {
	private MapPropertyBinding<String, PropertyValue> valuesBinding;
	
    public MapPropertyBinding<String, PropertyValue> values() {
        if (valuesBinding == null) {
        	valuesBinding = new MapPropertyBinding<String, PropertyValue>() {

				@Override
				protected Map<String, PropertyValue> doGetData() {
                    return PropertyDataBinding.this.getData().values;
				}

				@Override
				protected void doSetData(Map<String, PropertyValue> data) {
					PropertyDataBinding.this.getData().values = data;
				}
            };
            bind(valuesBinding);
        }
        return valuesBinding;
    }

	@Override
	protected PropertyData createInstance() {
		return new PropertyData();
	}

}
