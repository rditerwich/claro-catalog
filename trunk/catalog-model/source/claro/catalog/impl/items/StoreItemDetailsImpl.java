package claro.catalog.impl.items;

import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import org.apache.commons.fileupload.FileItem;

import claro.catalog.CatalogModelService;
import claro.catalog.command.items.ItemType;
import claro.catalog.command.items.StoreItemDetails;
import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.PropertyGroupAssignment;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.gwt.server.UploadServlet;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.SMap;

public class StoreItemDetailsImpl extends StoreItemDetails implements CommandImpl<StoreItemDetails.Result> {

	private static final long serialVersionUID = 1L;
	
	CatalogModel catalogModel;

	private StagingArea stagingArea;

	private OutputChannel outputChannel;

	@Override
	public Result execute() throws CommandException {
		catalogModel = CatalogModelService.getCatalogModel(catalogId);
		EntityManager em = JpaService.getEntityManager();
		
		if (stagingAreaId != null) {
			stagingArea = em.find(StagingArea.class, stagingAreaId);
		}
		
		if (outputChannelId != null) {
			outputChannel = em.find(OutputChannel.class, outputChannelId);
		}
		
		validateCommand(em);
		
		Result result = new Result();
		
		if (remove) {
			catalogModel.removeItem(itemId);
		} else {
			ItemModel itemModel;
			// New product or existing product?
			if (itemId == null) {
				if (itemType == ItemType.product) {
					itemModel = catalogModel.createProduct();
				} else {
					itemModel = catalogModel.createCategory();
				}
			} else {
				itemModel = catalogModel.getItem(itemId);
			}
			
			// Set categories
			if (parentsToSet != null || itemId == null) {
				List<ItemModel> categories = new ArrayList<ItemModel>();
				for (Long categoryId : CollectionUtil.notNull(parentsToSet)) {
					ItemModel categoryModel = catalogModel.getItem(categoryId);
					categories.add(categoryModel);
				}
				
				// Mare sure the item has a parent:
				if (categories.isEmpty()) {
					categories.add(catalogModel.getRootItem());
				}
				itemModel.setParents(categories);

			}
			
			// Groups to remove
			if (groupsToRemove != null) {
				itemModel.removeGroupAssignments(groupsToRemove);
			}
			
			// Properties to remove
			if (propertiesToRemove != null) {
				itemModel.removeProperties(propertiesToRemove);
			}
			
			// Properties to set
			if (propertiesToSet != null) {
				itemModel.setProperties(propertiesToSet);
			}
			
			// Groups to set
			if (groupsToSet != null) {
				itemModel.assignGroups(groupsToSet);
			}
			
			// Remove values:
			for (Entry<PropertyInfo, List<String>> value : CollectionUtil.notNull(valuesToRemove)) {
				PropertyModel propertyModel = itemModel.findProperty(value.getKey().propertyId, true);
				for (String language : value.getValue()) {
					propertyModel.removeValue(null, stagingArea, outputChannel, language);
				}
			}
			
			// Add values.
			for (Entry<PropertyInfo, SMap<String, Object>> value : CollectionUtil.notNull(valuesToSet)) {
				PropertyModel propertyModel = itemModel.findProperty(value.getKey().propertyId, true);
				for (Entry<String, Object> languageValue : value.getValue()) {
					Object typedValue = languageValue.getValue();
					if (propertyModel.getEntity().getType() == PropertyType.Media) {
						FileItem fileItem = UploadServlet.getUploadedFile(typedValue.toString());
						if (fileItem != null) {
							MediaValue mv = MediaValue.create(null, fileItem.getContentType(), fileItem.getName(), fileItem.get());
							typedValue = mv;
						}
					}
					propertyModel.setValue(null, stagingArea, outputChannel, languageValue.getKey(), typedValue);
				}
			}
			
			// Update result with new data.
			result.storedItemId = itemModel.getItemId();
			
			if (itemType == ItemType.product) {
				result.masterValues = ItemUtil.effectivePropertyValues(itemModel, stagingArea, outputChannel);
			}
			if (itemType == ItemType.catagory) {
				result.categoryLabels = ItemUtil.getNameLabels(itemModel, catalogModel, outputChannel, stagingArea);
			}
			
			result.parents = ItemUtil.parents(itemModel, catalogModel, stagingArea, outputChannel, false);
			result.parentExtentWithSelf = ItemUtil.parentExtent(itemModel, catalogModel, stagingArea, outputChannel, true);
			result.groups = ItemUtil.groups(itemModel, catalogModel, stagingArea, outputChannel);
			result.propertyData = ItemUtil.propertyData(catalogModel, itemModel, stagingArea, outputChannel);
		}
		
		
		return result;
	}

	
	private void validateCommand(EntityManager em) throws CommandValidationException {
		checkValid();
		
		if (itemId != null) {
			validate(catalogModel.getItem(itemId) != null);
			
			// must not delete root:
			if (remove) {
				validate(catalogModel.getItem(itemId) != catalogModel.getRootItem());
			}
		}

		// Set categories must exist.
		for (Long categoryId : CollectionUtil.notNull(parentsToSet)) {
			ItemModel categoryModel = catalogModel.getItem(categoryId);
			validate(categoryModel != null);
		}
		
		// removed properties must must exist and be ours
		for (Long propertyId : CollectionUtil.notNull(propertiesToRemove)) {
			validate (catalogModel.getItem(itemId).findProperty(propertyId, false) != null);
		}
		
		// properties set with non null id must exist and be ours.
		for (PropertyInfo property : CollectionUtil.notNull(propertiesToSet)) {
			validate (property.propertyId == null || catalogModel.getItem(itemId).findProperty(property.propertyId, false) != null);
		}
		
		// properties and groups set must exist
		for (Entry<PropertyInfo, PropertyGroupInfo> groupAssignment : CollectionUtil.notNull(groupsToRemove)) {
			PropertyModel propertyModel = catalogModel.getItem(itemId).findProperty(groupAssignment.getKey().propertyId, true);
			validate (propertyModel != null);

			PropertyGroupAssignment modelGroupAssignment = catalogModel.getItem(itemId).findGroupAssignment(propertyModel.getEntity());
			validate (modelGroupAssignment != null);
			
			validate (modelGroupAssignment.getCategory().getId().equals(propertyModel.getGroupAssignmentItemId()));
			validate (modelGroupAssignment.getProperty().equals(propertyModel.getEntity()));
			validate (modelGroupAssignment.getPropertyGroup().getId().equals(groupAssignment.getValue().propertyGroupId));
			validate (catalogModel.findPropertyGroupInfo(groupAssignment.getValue().propertyGroupId) != null);
		}
		
		// properties with non null ids and groups set must exist
		for (Entry<PropertyInfo, PropertyGroupInfo> groupAssignment : CollectionUtil.notNull(groupsToSet)) {
			validate (groupAssignment.getKey().propertyId == null || catalogModel.getItem(itemId).findProperty(groupAssignment.getKey().propertyId, true) != null);
			validate (catalogModel.findPropertyGroupInfo(groupAssignment.getValue().propertyGroupId) != null);
		}
		
		// properties for values to be removed should exist, as well as the values.
		for (Entry<PropertyInfo, List<String>> value : CollectionUtil.notNull(valuesToRemove)) {
			PropertyModel propertyModel = catalogModel.getItem(itemId).findProperty(value.getKey().propertyId, true);
			validate(propertyModel != null);
			for (String language : value.getValue()) {
				validate(propertyModel.getValues(stagingArea, outputChannel).get(language, SMap.undefined()) != SMap.undefined());
			}
		}
		
		// Properties for added values must exist.
		for (PropertyInfo value : CollectionUtil.notNull(valuesToSet).getKeys()) {
			if (itemId != null) {
				validate(catalogModel.getItem(itemId).findProperty(value.propertyId, true) != null);
			} else {
				// New item, so property must be defined on one of the parents:
				boolean found = false;
				List<Long> parents = new ArrayList<Long>(CollectionUtil.notNull(parentsToSet));
				if (parents.isEmpty()) {
					parents.add(catalogModel.getRootItem().getItemId());
				}
				for (Long categoryId : parents) {
					if (catalogModel.getItem(categoryId).findProperty(value.propertyId, true) != null) {
						found = true;
						break;
					}
				}
				validate(found);
			}
		}
	}

}
