package claro.catalog.data;

import java.io.Serializable;

import easyenterprise.lib.util.SMap;

public class ItemData implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final ItemData empty = new ItemData();

	public Long itemId;
	public ItemType type;
	public SMap<String, String> displayNames = SMap.empty();
	
	public ItemData() {
	}
	
	public ItemData(Long itemId, ItemType type, SMap<String, String> displayNames) {
		this.itemId = itemId;
		this.type = type;
		this.displayNames = displayNames;
	}
	
}
