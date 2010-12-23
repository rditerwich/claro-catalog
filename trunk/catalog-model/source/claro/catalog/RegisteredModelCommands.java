package claro.catalog;

import claro.catalog.impl.ItemDetailsCommandImpl;
import claro.catalog.impl.FindItemsImpl;
import claro.catalog.impl.RootPropertiesCommandImpl;
import claro.catalog.impl.importing.GetImportDefinitionsImpl;
import claro.catalog.impl.importing.UpdateImportDefinitionImpl;
import easyenterprise.lib.command.RegisteredCommands;

public class RegisteredModelCommands extends RegisteredCommands {
	public RegisteredModelCommands() {
		register(ItemDetailsCommandImpl.class);
		register(FindItemsImpl.class);
		register(RootPropertiesCommandImpl.class);
		register(GetImportDefinitionsImpl.class);
		register(UpdateImportDefinitionImpl.class);
	}
}
