package agilexs.catalogxsadmin.presentation.client.binding;

import agilexs.catalogxsadmin.presentation.client.Util;
import agilexs.catalogxsadmin.presentation.client.catalog.Item;
import agilexs.catalogxsadmin.presentation.client.catalog.ItemBinding;

public class ExtendedItemBinding<D extends Item> extends ItemBinding<D> {
	private PropertyBinding<String> nameBinding;
	private PropertyBinding<String> priceBinding;
	private String language;
	
	public void setLanguage(String language) {
		this.language = language;
		
		// TODO Update bindings?
		
	}
	
	public PropertyBinding<String> name() {
		if (nameBinding == null) {
			nameBinding = new PropertyBinding<String>() {
				protected String doGetData() {
					// TODO Can we cache PV?
					return Util.name(ExtendedItemBinding.this.getData(), language);
				}

				protected void doSetData(String data) {
					// TODO implement.
				}
			};
			
			bind(nameBinding);
		}
		
		return nameBinding;
	}
	
	public PropertyBinding<String> price() {
		if (priceBinding == null) {
			priceBinding = new PropertyBinding<String>() {
				protected String doGetData() {
					// TODO Can we cache PV?
					return Util.price(ExtendedItemBinding.this.getData());
				}
				
				protected void doSetData(String data) {
					// TODO implement.
				}
			};
			
			bind(priceBinding);
		}
		
		return priceBinding;
	}
}
