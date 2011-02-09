package claro.catalog.impl.items;

import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.List;
import java.util.Map.Entry;

import claro.catalog.CatalogModelService;
import claro.catalog.command.items.PerformStaging;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.StagingArea;
import claro.jpa.catalog.StagingStatus;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.util.SMap;

public class PerformStagingImpl extends PerformStaging implements CommandImpl<PerformStaging.Result> {

	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		
		CatalogModel model = CatalogModelService.getCatalogModel(catalogId);
		validate(model != null, "Catalog not found");
		
		StagingArea from = null;
		if (fromStagingName != null) {
			from = model.dao.findStagingAreaByName(fromStagingName);
			validate(from != null, "Staging area not found: " + fromStagingName);

		}
		StagingArea to = model.dao.findStagingAreaByName(toStagingName);
		validate(to != null, "Staging area not found: " + toStagingName);
	
		// remove all previous values
		model.dao.removeAllStagingValues(to);
		
		// get all output channels
		List<OutputChannel> outputChannels = model.getCatalog().getOutputChannels();
		
		// loop over all items
		for (Item item : model.getCatalog().getItems()) {
			ItemModel itemModel = model.getItem(item.getId());
			
			// loop over all properties
			for (Entry<PropertyGroupInfo, PropertyModel> entry : itemModel.getPropertyExtent()) {
				PropertyModel propertyModel = entry.getValue();
				
				// loop over output channels
				for (OutputChannel outputChannel : outputChannels) {
					
					// calculate effective values
					SMap<String,Object> effectiveValues = propertyModel.getEffectiveValues(from, outputChannel);
					
					// store them with to staging area
					for (Entry<String, Object> value : effectiveValues) {
						propertyModel.setValue(null, to, outputChannel, value.getKey(), value.getValue());
					}
				}
			}
		}
		
		// update staging status
		StagingStatus status = model.dao.getOrCreateStagingStatus(model.getCatalog(), to);
		status.setUpdateSequenceNr(status.getUpdateSequenceNr());
		
		return new Result();
	}
}