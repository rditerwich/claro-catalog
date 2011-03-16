package claro.catalog.command.items;

import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.List;

import com.google.common.base.Objects;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.SMap;

public class DisplayNames implements Command<DisplayNames.Result> {

	private static final long serialVersionUID = 1L;

	public Long catalogId;
	public Long stagingAreaId;
	public Long outputChannelId;
	
	public List<Long> itemIds;
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DisplayNames) {
			DisplayNames other = (DisplayNames) obj;
			return Objects.equal(this.catalogId, other.catalogId) 
				&& Objects.equal(this.itemIds, other.itemIds) 
				&& Objects.equal(this.stagingAreaId, other.stagingAreaId) 
				&& Objects.equal(outputChannelId, other.outputChannelId);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(catalogId, itemIds, stagingAreaId, outputChannelId);
	}

	@Override
	public void checkValid() throws CommandValidationException {
		validate(catalogId != null);
		for (Long itemId : itemIds) {
			validate(itemId != null);
		}
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;

		public SMap<Long, SMap<String, String>> items;
	}
}
