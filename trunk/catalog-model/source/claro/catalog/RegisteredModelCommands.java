package claro.catalog;

import claro.catalog.impl.RootPropertiesCommandImpl;
import claro.catalog.impl.importing.GetImportSourcesImpl;
import claro.catalog.impl.importing.PerformImportImpl;
import claro.catalog.impl.importing.StoreImportSourceImpl;
import claro.catalog.impl.items.FindItemsImpl;
import claro.catalog.impl.items.FindPropertiesImpl;
import claro.catalog.impl.items.GetCategoryTreeImpl;
import claro.catalog.impl.items.ItemDetailsCommandImpl;
import easyenterprise.lib.command.RegisteredCommands;

public class RegisteredModelCommands extends RegisteredCommands {
	public RegisteredModelCommands() {
		register(GetCategoryTreeImpl.class);
		register(ItemDetailsCommandImpl.class);
		register(FindItemsImpl.class);
		register(FindPropertiesImpl.class);
		register(RootPropertiesCommandImpl.class);
		register(GetImportSourcesImpl.class);
		register(StoreImportSourceImpl.class);
		register(PerformImportImpl.class);
	}
}
