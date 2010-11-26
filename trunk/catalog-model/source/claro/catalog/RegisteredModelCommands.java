package claro.catalog;

import claro.catalog.impl.ItemDetailsCommandImpl;
import easyenterprise.lib.command.RegisteredCommands;

public class RegisteredModelCommands extends RegisteredCommands {
	public RegisteredModelCommands() {
		register(ItemDetailsCommandImpl.class);
	}
}
