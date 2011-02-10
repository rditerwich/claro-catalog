package claro.catalog;

import claro.catalog.impl.FlushCatalogModelImpl;
import claro.catalog.impl.GetLanguagesByShopImpl;
import claro.catalog.impl.GetLanguagesImpl;
import claro.catalog.impl.LoginImpl;
import claro.catalog.impl.RootDataCommandImpl;
import claro.catalog.impl.importing.DeleteImportedDataImpl;
import claro.catalog.impl.importing.GetImportSourceHistoryImpl;
import claro.catalog.impl.importing.GetImportSourcesImpl;
import claro.catalog.impl.importing.PerformImportImpl;
import claro.catalog.impl.importing.StoreImportSourceImpl;
import claro.catalog.impl.items.FindItemsImpl;
import claro.catalog.impl.items.FindPropertiesImpl;
import claro.catalog.impl.items.GetCategoryTreeImpl;
import claro.catalog.impl.items.ItemDetailsCommandImpl;
import claro.catalog.impl.items.PerformStagingImpl;
import claro.catalog.impl.items.StoreItemDetailsImpl;
import claro.catalog.impl.order.GetOrdersImpl;
import claro.catalog.impl.shop.GetShopsImpl;
import claro.catalog.impl.shop.StoreShopImpl;
import easyenterprise.lib.command.RegisteredCommands;

public class RegisteredCatalogServerCommands extends RegisteredCommands {
	public RegisteredCatalogServerCommands() {
		register(GetCategoryTreeImpl.class);
		register(ItemDetailsCommandImpl.class);
		register(FindItemsImpl.class);
		register(FindPropertiesImpl.class);
		register(StoreItemDetailsImpl.class);
		register(RootDataCommandImpl.class);
		register(GetImportSourcesImpl.class);
		register(GetImportSourceHistoryImpl.class);
		register(StoreImportSourceImpl.class);
		register(DeleteImportedDataImpl.class);
		register(PerformImportImpl.class);
		register(GetLanguagesImpl.class);
		register(GetLanguagesByShopImpl.class);
		register(GetShopsImpl.class);
		register(StoreShopImpl.class);
		register(GetOrdersImpl.class);
		register(LoginImpl.class);
		register(PerformStagingImpl.class);
		register(FlushCatalogModelImpl.class);
	}
}
