package claro.catalog.command.items;


import static claro.catalog.model.CatalogModelTestUtil.addCategory;
import static claro.catalog.model.CatalogModelTestUtil.addGroup;
import static claro.catalog.model.CatalogModelTestUtil.addProduct;

import java.sql.SQLException;
import java.util.Collections;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import claro.catalog.command.items.StoreItemDetails.Result;
import claro.catalog.data.ItemType;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.impl.items.ItemUtil;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Product;
import claro.jpa.catalog.PropertyGroup;
import claro.jpa.catalog.PropertyType;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.Money;
import easyenterprise.lib.util.SMap;
import easyenterprise.lib.util.Tuple;

public class StoreItemDetailsTest extends CatalogTestBase {
	
	private Long product1Id;
	private Long printersCatId;
	private Long inkCatId;
	protected Long hpPrintersCatId;
	private Long pricesGroupId;
	private String rootName;
	private Long hpInkId;
	
	
	@Test
	public void basicCreateProductTest() throws Exception {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.catalogId = TEST_CATALOG_ID;
		cmd.itemType = ItemType.product;
		cmd.valuesToSet = SMap.create(getCatalogModel().nameProperty.getPropertyInfo(), SMap.<String, Object>create(null, "New Product"));
		
		Result result = executeCommand(cmd);
		
		// Has the result been filled?
		Assert.assertNotNull(result.storedItemId);
		
		// make sure it is in the DB too.
		Assert.assertNotNull(getCatalogDao().getEntityManager().find(Product.class, result.storedItemId));
	}
	
	
	@Test
	public void basicRemoveProductTest() throws Exception {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.catalogId = TEST_CATALOG_ID;
		cmd.itemType = ItemType.product;
		cmd.itemId = product1Id;
		cmd.remove = true;
		
		Result result = executeCommand(cmd);
		
		// Has the result been filled?
		Assert.assertNull(result.storedItemId);
		
		// make sure it is removed from the DB.
		Assert.assertNull(getCatalogDao().getEntityManager().find(Product.class, product1Id));
		
		// And the model?
		ItemModel parent = getCatalogModel().getItem(hpPrintersCatId);
		for (ItemModel child : parent.getChildExtent()) {
			Assert.assertNotSame(product1Id, child.getItemId());
		}
	}
	
	@Test
	public void removeConnectedCategory() throws Exception {
		// TODO
		// Check whether the first parent is added as a parent to the children.
	}
	
	@Test
	public void basicSetProperty() throws Exception {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.catalogId = TEST_CATALOG_ID;
		cmd.itemType = ItemType.category;
		cmd.itemId = inkCatId;
		PropertyInfo colorProperty = new PropertyInfo();
		colorProperty.labels = SMap.create(null, "color");
		colorProperty.setType(PropertyType.String);
		cmd.propertiesToSet = Collections.singletonList(colorProperty);
		
		Result result = executeCommand(cmd);
		
		// Has the result been filled?
		Assert.assertEquals(inkCatId, result.storedItemId);
		
		// The property should automatically be tied to the general property group:
		SMap<PropertyInfo, PropertyData> generalGroupProperties = result.propertyData.get(new PropertyGroupInfo(getCatalogModel().generalPropertyGroup.getId()));
		Assert.assertNotNull(generalGroupProperties);
		
		// Find the info back, it should have no values attached to it.
		PropertyData resultColorData = generalGroupProperties.get(colorProperty);
		Assert.assertNotNull(resultColorData);
		Assert.assertTrue(CollectionUtil.notNull(resultColorData.values).isEmpty());
		Assert.assertTrue(CollectionUtil.notNull(resultColorData.effectiveValues).isEmpty());
		Assert.assertTrue(CollectionUtil.notNull(resultColorData.sourceValues).isEmpty());
		
		// make sure it is in the DB too. TODO
//		Assert.assertNotNull(JpaService.getEntityManager().find(Product.class, result.storedProductId));
	}
	
	@Test
	public void basicRemoveProperty() throws Exception {
		// TODO
		
		// check Values have been removed too.
	}
	
	@Test
	public void basicSetParents() throws Exception {
		// TODO
	}
	
	@Test
	public void setParentsCycle() throws Exception {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.catalogId = TEST_CATALOG_ID;
		cmd.itemType = ItemType.category;
		cmd.itemId = printersCatId;
		
		// Request a cycle:
		cmd.parentsToSet = Collections.singletonList(hpPrintersCatId);
		
		boolean exceptionCaught = false;
		try {
			executeCommand(cmd);
		} catch (CommandValidationException ex) {
			exceptionCaught = true;
		}
		if (!exceptionCaught) {
			Assert.fail("Command must throw a cycle detected validation exception");
		}
	}
	
	@Test
	public void basicRemoveParents() throws Exception {
		// TODO
	}
	
	@Test
	public void basicSetGroups() throws Exception {
		
		PropertyInfo imageProperty = new PropertyInfo();
		imageProperty.propertyId = getCatalogModel().imageProperty.getPropertyId();
		
		
		// Verify the group:
		PropertyModel existingImagePropertyData = getCatalogModel().getItem(inkCatId).findProperty(imageProperty.propertyId, true);
		Assert.assertEquals(getCatalogModel().getRootItem().getItemId(), existingImagePropertyData.getGroupAssignmentItemId());
		
		// Change the group
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.catalogId = TEST_CATALOG_ID;
		cmd.itemType = ItemType.category;
		cmd.itemId = inkCatId;
		cmd.groupsToSet = SMap.create(imageProperty, new PropertyGroupInfo(getCatalogModel().generalPropertyGroup.getId()));

		// Execute the command
		Result result = executeCommand(cmd);
		
		// Has the result been filled?
		Assert.assertEquals(inkCatId, result.storedItemId);
		
		// The property should now be tied to the general property group:
		SMap<PropertyInfo, PropertyData> generalGroupProperties = result.propertyData.get(new PropertyGroupInfo(getCatalogModel().generalPropertyGroup.getId()));
		Assert.assertNotNull(generalGroupProperties);
		
		// Find the model for the property, it should have new group info.
		PropertyData resultImageData = generalGroupProperties.get(imageProperty);
		Assert.assertNotNull(resultImageData);

		Assert.assertEquals(inkCatId, resultImageData.groupAssignmentItemId);
		Assert.assertEquals(ItemUtil.getNameLabels(getCatalogModel().getItem(inkCatId), getCatalogModel(), null, null).get(), resultImageData.groupItemNameLabels.get());
	}
	
	@Test
	public void basicRemoveGroups() throws Exception {
		
		PropertyInfo priceProperty = new PropertyInfo();
		priceProperty.propertyId = getCatalogModel().priceProperty.getPropertyId();
		
		
		// Verify the group:
		PropertyModel existingPricePropertyData = getCatalogModel().getItem(hpPrintersCatId).findProperty(priceProperty.propertyId, true);
		Assert.assertEquals(hpPrintersCatId, existingPricePropertyData.getGroupAssignmentItemId());
		
		// Change the group
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.catalogId = TEST_CATALOG_ID;
		cmd.itemType = ItemType.category;
		cmd.itemId = hpPrintersCatId;
		cmd.groupsToRemove = SMap.create(priceProperty, new PropertyGroupInfo(pricesGroupId));

		// Execute the command
		Result result = executeCommand(cmd);
		
		// Has the result been filled?
		Assert.assertEquals(hpPrintersCatId, result.storedItemId);
		
		// The property should now be tied to the general property group, through the root:
		SMap<PropertyInfo, PropertyData> generalGroupProperties = result.propertyData.get(new PropertyGroupInfo(getCatalogModel().generalPropertyGroup.getId()));
		Assert.assertNotNull(generalGroupProperties);
		
		// Find the model for the property, it should have new group info.
		PropertyData resultPriceData = generalGroupProperties.get(priceProperty);
		Assert.assertNotNull(resultPriceData);

		Assert.assertEquals(getCatalogModel().getRootItem().getItemId(), resultPriceData.groupAssignmentItemId);
	}

	@Test
	public void basicSetPropertyValue() throws Exception {
		StoreItemDetails cmd = new StoreItemDetails();
		
		PropertyInfo propertyToSet = getCatalogModel().variantProperty.getPropertyInfo();
		Object valueToSet = "New Value";
		
		ItemModel inkCat = getCatalogModel().getItem(inkCatId);
		PropertyModel variantProperty = inkCat.findProperty(propertyToSet.propertyId, true);
		Assert.assertNotSame(valueToSet, variantProperty.getValues(null, null).get());
		
		cmd.catalogId = TEST_CATALOG_ID;
		cmd.itemType = ItemType.category;
		cmd.itemId = inkCatId;
		cmd.valuesToSet = SMap.create(propertyToSet, SMap.create((String)null, valueToSet)); // Remove value for name.
		
		Result result = executeCommand(cmd);
		
		// Has the result been filled?
		Assert.assertEquals(inkCatId, result.storedItemId);
		
		PropertyData resultVariantData = findProperty(result, getCatalogModel().generalPropertyGroup, propertyToSet);

		// Find the info back, it should have the new value set.
		Assert.assertNotNull(resultVariantData);
		Assert.assertEquals(valueToSet, resultVariantData.values.get().get());
		
		// TODO Check DB as well?
	}

	@Test
	public void setPropertyValueCheckInherited() throws Exception {
		Object valueToSet = "New Value";

		FindItems find = new FindItems();
		find.catalogId = TEST_CATALOG_ID;
		
		FindItems.Result findResult = executeCommand(find);

		SMap<PropertyInfo, SMap<String, Object>> hpInk = findResult.items.get(hpInkId);
		Assert.assertNotNull(hpInk);
		
		Object inheritedValue = hpInk.getOrEmpty(getCatalogModel().supplierProperty.getPropertyInfo()).get(null);
		Assert.assertNotSame(valueToSet, inheritedValue);

		StoreItemDetails cmd = new StoreItemDetails();
		
		PropertyInfo propertyToSet = getCatalogModel().supplierProperty.getPropertyInfo();
		
		ItemModel inkCat = getCatalogModel().getItem(inkCatId);
		PropertyModel variantProperty = inkCat.findProperty(propertyToSet.propertyId, true);
		Assert.assertNotSame(valueToSet, variantProperty.getValues(null, null).get());
		
		cmd.catalogId = TEST_CATALOG_ID;
		cmd.itemType = ItemType.category;
		cmd.itemId = inkCatId;
		cmd.valuesToSet = SMap.create(propertyToSet, SMap.create((String)null, valueToSet)); 
		
		Result result = executeCommand(cmd);
		
		// Has the result been filled?
		Assert.assertEquals(inkCatId, result.storedItemId);
		
		PropertyData resultVariantData = findProperty(result, getCatalogModel().generalPropertyGroup, propertyToSet);
		
		// Find the info back, it should have the new value set.
		Assert.assertNotNull(resultVariantData);
		Assert.assertEquals(valueToSet, resultVariantData.values.get().get());

		find = new FindItems();
		find.catalogId = TEST_CATALOG_ID;
		
		findResult = executeCommand(find);

		hpInk = findResult.items.get(hpInkId);
		Assert.assertNotNull(hpInk);
		
		inheritedValue = hpInk.getOrEmpty(getCatalogModel().supplierProperty.getPropertyInfo()).get(null);
		Assert.assertEquals(valueToSet, inheritedValue);
		
	}
	

	private PropertyData findProperty(Result result, PropertyGroup generalPropertyGroup, PropertyInfo nameProperty) {
		SMap<PropertyInfo, PropertyData> generalGroupProperties = result.propertyData.get(new PropertyGroupInfo(generalPropertyGroup.getId()));
		return generalGroupProperties.get(nameProperty);
	}
	
	
	@Test
	public void basicRemovePropertyValue() throws Exception {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.catalogId = TEST_CATALOG_ID;
		cmd.itemType = ItemType.category;
		cmd.itemId = inkCatId;
		PropertyInfo propertyOfValueToRemove = getCatalogModel().nameProperty.getPropertyInfo();
		cmd.valuesToRemove = SMap.create(propertyOfValueToRemove, Collections.singletonList((String)null)); // Remove value for name.
		
		Result result = executeCommand(cmd);
		
		// Has the result been filled?
		Assert.assertEquals(inkCatId, result.storedItemId);
		
		// Find the info back, it should have no values attached to it.
		PropertyData resultNameData = findProperty(result, getCatalogModel().generalPropertyGroup, propertyOfValueToRemove);
		Assert.assertNotNull(resultNameData);
		Assert.assertTrue(CollectionUtil.notNull(resultNameData.values).isEmpty());
		Assert.assertEquals(rootName, resultNameData.effectiveValues.get().get().get());
	}
	
	@Test
	public void createDuplicateProduct() throws Exception {
		// TODO
	}
	
	@Test
	public void setPropertyAndValue() throws Exception {
		// TODO
	}
	
	@Test
	public void setPropertyValuesLanguageSpecific() throws Exception {
		// TODO
	}
	
	
	@Test
	public void setPropertyValuesOutputSourceSpecific() throws Exception {
		// TODO
	}
	
	
	
	@Before
	@SuppressWarnings("unchecked")
	public void setupItems() throws Exception {
		ensureDatabaseCreated();
		getCatalogDao().startTransaction();
			
		try {
			CatalogModel model = getCatalogModel();
			EntityManager entityManager = getCatalogDao().getEntityManager();
			
			// First create some categories and items
			Category root = model.catalog.getRoot();
			rootName = (String) model.getRootItem().findProperty(model.nameProperty.getPropertyId(), false).getValues(null, null).get();
			Category printers = addCategory(entityManager, model, root, "Printers");
			Category ink = addCategory(entityManager, model, root, "Ink");
			Category hpPrinters = addCategory(entityManager, model, printers, "HP Printers");
			
			PropertyGroup prices = addGroup(entityManager, model, "Prices");
			pricesGroupId = prices.getId();
			
			// assign a property to the new group
			getCatalogDao().createGroupAssignment(getCatalogModel().priceProperty.getEntity(), prices, hpPrinters);
			
			printersCatId = printers.getId();
			hpPrintersCatId = hpPrinters.getId();
			inkCatId = ink.getId();
			
			product1Id = addProduct(entityManager, model, hpPrinters, "HP Deskjet 540 B&W", new Tuple[] { 
					Tuple.create(model.articleNumberProperty, "ART123123123"),
					Tuple.create(model.variantProperty, "HP Deskjet 540 Color"),
					Tuple.create(model.priceProperty, new Money(300.00, "EUR")),
			}).getId();
			
			addProduct(entityManager, model, hpPrinters, "HP Deskjet 888 ", new Tuple[] { 
					Tuple.create(model.articleNumberProperty, "ART123123123"),
					Tuple.create(model.variantProperty, "HP Deskjet 888 Color"),
					Tuple.create(model.priceProperty, new Money(300.00, "EUR")),
			});
			
			addProduct(entityManager, model, printers, "Canon Bla", new Tuple[] { 
					Tuple.create(model.articleNumberProperty, "ART111222333"),
					Tuple.create(model.variantProperty, "Canon Bla2"),
					Tuple.create(model.priceProperty, new Money(200.00, "EUR")),
			});
			
			hpInkId = addProduct(entityManager, model, ink, "HP CYM ", new Tuple[] { 
					Tuple.create(model.articleNumberProperty, "ART123123111"),
					Tuple.create(model.variantProperty, "HP CYM XL"),
					Tuple.create(model.priceProperty, new Money(60.00, "EUR")),
			}).getId();
			
			addProduct(entityManager, model, ink, "Canon Cyan", new Tuple[] { 
					Tuple.create(model.articleNumberProperty, "ART111222444"),
					Tuple.create(model.variantProperty, "Canon Cyan XL"),
					Tuple.create(model.priceProperty, new Money(20.00, "EUR")),
			});
			
			addProduct(entityManager, model, ink, "Canon Magenta", new Tuple[] { 
					Tuple.create(model.articleNumberProperty, "ART111222555"),
					Tuple.create(model.variantProperty, "Canon Megenta XL"),
					Tuple.create(model.priceProperty, new Money(20.00, "EUR")),
			});
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			getCatalogDao().commitTransaction();
		}
	}

}
