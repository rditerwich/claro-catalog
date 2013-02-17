package claro.catalog.command.items;

import java.util.List;
import java.util.Map;

import claro.catalog.data.ItemData;

import com.google.common.base.Objects;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.SMap;

public class GetItemTree implements Command<GetItemTree.Result> {

	private static final long serialVersionUID = 1L;

	public Long catalogId;
	public Long rootId;
	
	/**
	 * The number of levels deep to return.  -1 means infinite.
	 */
	public int depth = -1;
	public boolean categoriesOnly = true;
	
	public Long stagingAreaId;
	public Long outputChannelId;
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GetItemTree) {
			GetItemTree other = (GetItemTree) obj;
			return Objects.equal(this.catalogId, other.catalogId) 
				&& Objects.equal(this.rootId, other.rootId) 
				&& Objects.equal(this.depth, other.depth) 
				&& Objects.equal(this.categoriesOnly, other.categoriesOnly) 
				&& Objects.equal(this.stagingAreaId, other.stagingAreaId) 
				&& Objects.equal(outputChannelId, other.outputChannelId);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(catalogId,rootId, depth, categoriesOnly, stagingAreaId, outputChannelId);
	}

	@Override
	public void checkValid() throws CommandValidationException {
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;

		public Long root;
		public SMap<Long, ItemData> items;
		public Map<Long, List<Long>> children;
	}
}
