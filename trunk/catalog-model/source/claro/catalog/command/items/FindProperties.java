package claro.catalog.command.items;

import static com.google.common.base.Objects.equal;

import java.util.List;

import claro.catalog.data.PropertyInfo;

import com.google.common.base.Objects;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;

public class FindProperties implements Command<FindProperties.Result> {

	private static final long serialVersionUID = 1L;
	
	public Long catalogId;
	
	public void checkValid() throws CommandValidationException {
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FindProperties) {
			FindProperties other = (FindProperties) obj;
			return equal(catalogId, other.catalogId);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(catalogId);
	}
	
	public static class Result implements CommandResult {

		private static final long serialVersionUID = 1L;

		public List<PropertyInfo> properties;
	}
}
