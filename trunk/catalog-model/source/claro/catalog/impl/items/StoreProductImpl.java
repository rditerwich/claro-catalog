package claro.catalog.impl.items;

import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import org.apache.commons.fileupload.FileItem;

import claro.catalog.CatalogModelService;
import claro.catalog.command.items.StoreProduct;
import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyInfo;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.gwt.server.UploadServlet;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.SMap;

public class StoreProductImpl extends StoreProduct implements CommandImpl<StoreProduct.Result> {

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
			catalogModel.removeItem(productId);
		} else {
			ItemModel productModel;
			// New product or existing product?
			if (productId == null) {
				productModel = catalogModel.createProduct();
			} else {
				productModel = catalogModel.getItem(productId);
			}
			
			// Set categories
			if (categoriesToSet != null) {
				List<ItemModel> categories = new ArrayList<ItemModel>();
				for (Long categoryId : categoriesToSet) {
					ItemModel categoryModel = catalogModel.getItem(categoryId);
					categories.add(categoryModel);
				}
				productModel.setParents(categories);
			}
			
			// Remove values:
			for (Entry<PropertyInfo, List<String>> value : CollectionUtil.notNull(valuesToRemove)) {
				PropertyModel propertyModel = productModel.findProperty(value.getKey().propertyId, true);
				for (String language : value.getValue()) {
					propertyModel.removeValue(stagingArea, outputChannel, language);
				}
			}
			
			// Add values.
			for (Entry<PropertyInfo, SMap<String, Object>> value : CollectionUtil.notNull(valuesToSet)) {
				PropertyModel propertyModel = productModel.findProperty(value.getKey().propertyId, true);
				for (Entry<String, Object> languageValue : value.getValue()) {
					Object typedValue = languageValue.getValue();
					if (propertyModel.getEntity().getType() == PropertyType.Media) {
						FileItem fileItem = UploadServlet.getUploadedFile(typedValue.toString());
						if (fileItem != null) {
							MediaValue mv = new MediaValue(null, fileItem.getContentType(), fileItem.getName());
							typedValue = mv;
						}
					}
					propertyModel.setValue(stagingArea, outputChannel, languageValue.getKey(), typedValue);
				}
			}
			
			// Update result with new data.
			result.storedProductId = productModel.getItemId();
			result.masterValues = ItemUtil.effectivePropertyValues(productModel, stagingArea, outputChannel);
			result.detailValues = ItemUtil.propertyData(productModel, stagingArea, outputChannel);
			result.categories = ItemUtil.parents(productModel, catalogModel, stagingArea, outputChannel);
		}
		
		
		return result;
	}

	
	private void validateCommand(EntityManager em) throws CommandValidationException {
		checkValid();
		
		if (productId != null) {
			validate(catalogModel.getItem(productId) != null);
			
			// must not delete root:
			if (remove) {
				validate(catalogModel.getItem(productId) != catalogModel.getRootItem());
			}
		}

		// Set categories must exist.
		for (Long categoryId : CollectionUtil.notNull(categoriesToSet)) {
			ItemModel categoryModel = catalogModel.getItem(categoryId);
			validate(categoryModel != null);
		}
		
		// properties for values to be removed should exist, as well as the values.
		for (Entry<PropertyInfo, List<String>> value : CollectionUtil.notNull(valuesToRemove)) {
			PropertyModel propertyModel = catalogModel.getItem(productId).findProperty(value.getKey().propertyId, true);
			validate(propertyModel != null);
			for (String language : value.getValue()) {
				validate(propertyModel.getValues(stagingArea, outputChannel).get(language, SMap.undefined()) != SMap.undefined());
			}
		}
		
		// Properties for added values must exist.
		for (PropertyInfo value : CollectionUtil.notNull(valuesToSet).getKeys()) {
			if (productId != null) {
				validate(catalogModel.getItem(productId).findProperty(value.propertyId, true) != null);
			} else {
				// New item, so property must be defined on one of the parents:
				boolean found = false;
				for (Long categoryId : CollectionUtil.notNull(categoriesToSet)) {
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
