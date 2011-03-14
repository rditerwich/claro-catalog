package claro.catalog.impl.items;

import static com.google.common.base.Objects.equal;
import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.ArrayList;
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
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;
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
		
		int count1 = 0;
		int count2 = 0;
		// loop over all items
		for (Item item : new ArrayList<Item>(model.getCatalog().getItems())) {
			System.out.println("Staging item " + item.getId());
			ItemModel itemModel = model.getItem(item.getId());
			Item itemEntity = itemModel.getEntity();
			
			// allow failing items
			try {
				// loop over all properties
				for (Entry<PropertyGroupInfo, PropertyModel> entry : itemModel.getPropertyExtent()) {
					PropertyModel propertyModel = entry.getValue();
					Property propertyEntity = propertyModel.getEntity();
					
					// loop over output channels
					for (OutputChannel outputChannel : outputChannels) {
						
						// calculate effective values
						SMap<String,Object> effectiveValues = propertyModel.getEffectiveValues(from, outputChannel);
						
						// store them with to staging area
						for (Entry<String, Object> value : effectiveValues) {
							String language = value.getKey();
							Object typedValue = value.getValue();
							count1++;
							
							// skip values whose default-language has the same value
							if (language != null && equal(effectiveValues.get(null), typedValue)) continue;
							
							PropertyValue propertyValue = new PropertyValue();
							itemEntity.getPropertyValues().add(propertyValue);
							propertyValue.setItem(itemEntity);
							
							propertyValue.setProperty(propertyEntity);
							propertyValue.setSource(null);
							propertyValue.setStagingArea(to);
							propertyValue.setOutputChannel(outputChannel);
							propertyValue.setLanguage(language);
							PropertyModel.setTypedValue(model, propertyValue, typedValue);
							model.dao.getEntityManager().persist(propertyValue);
							count2++;
						}
					}
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
				// continue with next item
			}
			
			model.invalidateItem(itemModel);
		}
		
		// update staging status
		StagingStatus status = model.dao.getOrCreateStagingStatus(model.getCatalog(), to);
		status.setUpdateSequenceNr(status.getUpdateSequenceNr() + 1);
		
		// Flush cache
		model.flush();
		System.out.println("Has Transaction: " + model.dao.getEntityManager().getTransaction().isActive());
		model.dao.getEntityManager().flush();
		model.dao.getEntityManager().clear();
		System.out.println("Created " + count2 + " values for staging (of a total of " + count1 + " values)");
		return new Result();
	}
}