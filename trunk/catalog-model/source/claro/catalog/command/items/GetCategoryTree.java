package claro.catalog.command.items;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.SMap;

public class GetCategoryTree implements Command<GetCategoryTree.Result> {

	public Long catalogId;
	public Long stagingAreaId;
	public Long outputChannelId;

	@Override
	public void checkValid() throws CommandValidationException {
		// TODO Auto-generated method stub
		
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;

		public Long root;
		public SMap<Long, SMap<String, String>> categories;
		public SMap<Long, Long> children;
	}
}
