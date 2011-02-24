package claro.catalog.command.items;

import com.google.common.base.Objects;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.SMap;

public class GetCategoryTree implements Command<GetCategoryTree.Result> {

	private static final long serialVersionUID = 1L;

	public Long catalogId;
	public Long stagingAreaId;
	public Long outputChannelId;
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GetCategoryTree) {
			GetCategoryTree other = (GetCategoryTree) obj;
			return Objects.equal(this.catalogId, other.catalogId) && Objects.equal(this.stagingAreaId, other.stagingAreaId) && Objects.equal(outputChannelId, other.outputChannelId);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(catalogId, stagingAreaId, outputChannelId);
	}

	@Override
	public void checkValid() throws CommandValidationException {
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;

		public Long root;
		public SMap<Long, SMap<String, String>> categories;
		public SMap<Long, Long> children;
	}
}
