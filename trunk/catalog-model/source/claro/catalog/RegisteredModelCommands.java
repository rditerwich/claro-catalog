package claro.catalog;

import claro.catalog.impl.ItemDetailsCommandImpl;
import claro.catalog.impl.ProductListCommandImpl;
import claro.catalog.impl.RootPropertiesCommandImpl;
import claro.catalog.impl.importing.UpdateImportDefinitionImpl;
import easyenterprise.lib.command.RegisteredCommands;

public class RegisteredModelCommands extends RegisteredCommands {
	public RegisteredModelCommands() {
		register(ItemDetailsCommandImpl.class);
		register(ProductListCommandImpl.class);
		register(RootPropertiesCommandImpl.class);
		register(UpdateImportDefinitionImpl.class);
	}
}
