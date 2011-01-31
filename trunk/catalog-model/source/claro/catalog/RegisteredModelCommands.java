package claro.catalog;

import claro.catalog.impl.LoginImpl;
import claro.catalog.impl.RootDataCommandImpl;
import claro.catalog.impl.importing.GetImportSourceHistoryImpl;
import claro.catalog.impl.importing.GetImportSourcesImpl;
import claro.catalog.impl.importing.PerformImportImpl;
import claro.catalog.impl.importing.StoreImportSourceImpl;
import claro.catalog.impl.items.FindItemsImpl;
import claro.catalog.impl.items.FindPropertiesImpl;
import claro.catalog.impl.items.GetCategoryTreeImpl;
import claro.catalog.impl.items.ItemDetailsCommandImpl;
import claro.catalog.impl.items.StoreItemDetailsImpl;
import easyenterprise.lib.command.RegisteredCommands;

public class RegisteredModelCommands extends RegisteredCommands {
	public RegisteredModelCommands() {
		register(GetCategoryTreeImpl.class);
		register(ItemDetailsCommandImpl.class);
		register(FindItemsImpl.class);
		register(FindPropertiesImpl.class);
		register(StoreItemDetailsImpl.class);
		register(RootDataCommandImpl.class);
		register(GetImportSourcesImpl.class);
		register(GetImportSourceHistoryImpl.class);
		register(StoreImportSourceImpl.class);
		register(PerformImportImpl.class);
		register(LoginImpl.class);
	}
}
