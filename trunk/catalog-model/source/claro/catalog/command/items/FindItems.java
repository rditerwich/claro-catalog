package claro.catalog.command.items;

import java.util.List;
import java.util.Map;

import claro.catalog.data.PropertyInfo;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.Paging;
import easyenterprise.lib.util.SMap;

public class FindItems implements Command<FindItems.Result> {

	private static final long serialVersionUID = 1L;
	
	public enum ResultType { products, catagories, items }

	public ResultType resultType = ResultType.items;
	
	public Long catalogId;
	public Long stagingAreaId;
	public Long outputChannelId;
	
	public String uiLanguage;
	public String language;
	
	public List<Long> categoryIds;
	public String filter;

	public List<Long> orderByIds;
	
	public Paging paging = Paging.NO_PAGING;
	
	public void checkValid() throws CommandValidationException {
	}
	
	public static class Result implements CommandResult {

		private static final long serialVersionUID = 1L;

		public SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> items;
	}
}
