package claro.catalog.manager.client.items;

import claro.jpa.catalog.OutputChannel;

public class ItemPageModel {
	private String selectedLanguage;
	private OutputChannel selectedOutputChannel;

	public void setSelectedLanguage(String selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}

	public void setSelectedOutputChannel(OutputChannel selectedShop) {
		this.selectedOutputChannel = selectedShop;
	}
	
	public String getSelectedLanguage() {
		return selectedLanguage;
	}
	public OutputChannel getSelectedOutputChannel() {
		return selectedOutputChannel;
	}
	
	public Long getSelectedOutputChannelId() {
		if (selectedOutputChannel != null) {
			return selectedOutputChannel.getId();
		}
		return null;
	}

}
