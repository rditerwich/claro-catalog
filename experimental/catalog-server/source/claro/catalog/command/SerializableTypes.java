package claro.catalog.command;

import claro.catalog.data.MediaValue;

import com.google.gwt.user.client.rpc.IsSerializable;

import easyenterprise.lib.util.Money;

public class SerializableTypes implements IsSerializable {
	public Money m;
	public MediaValue mv;

}
