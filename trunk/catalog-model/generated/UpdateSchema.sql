BEGIN;
CREATE SCHEMA catalog;
COMMIT;

BEGIN;
CREATE TABLE catalog.party();
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD COLUMN name VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN name TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN name SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD COLUMN phonenumber VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN phonenumber TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN phonenumber SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD COLUMN website VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN website TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN website DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD COLUMN billingname VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN billingname TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN billingname DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD COLUMN address_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN address_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN address_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD COLUMN shippingaddress_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN shippingaddress_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN shippingaddress_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD COLUMN deliveryaddress_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN deliveryaddress_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN deliveryaddress_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD COLUMN billingaddress_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN billingaddress_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ALTER COLUMN billingaddress_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.user();
COMMIT;

BEGIN;
ALTER TABLE catalog.user ADD COLUMN email VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ALTER COLUMN email TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ALTER COLUMN email SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ADD COLUMN password VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ALTER COLUMN password TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ALTER COLUMN password SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ADD COLUMN party_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ALTER COLUMN party_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ALTER COLUMN party_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ADD COLUMN iscataloguser BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ALTER COLUMN iscataloguser TYPE BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ALTER COLUMN iscataloguser SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.emailconfirmation();
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ADD COLUMN email VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ALTER COLUMN email TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ALTER COLUMN email SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ADD COLUMN confirmationkey VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ALTER COLUMN confirmationkey TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ALTER COLUMN confirmationkey SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ADD COLUMN expirationtime BIGINT;
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ALTER COLUMN expirationtime TYPE BIGINT;
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ALTER COLUMN expirationtime SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.emailconfirmation ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.address();
COMMIT;

BEGIN;
ALTER TABLE catalog.address ADD COLUMN address1 VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ALTER COLUMN address1 TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ALTER COLUMN address1 SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ADD COLUMN address2 VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ALTER COLUMN address2 TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ALTER COLUMN address2 DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ADD COLUMN town VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ALTER COLUMN town TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ALTER COLUMN town SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ADD COLUMN postalcode VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ALTER COLUMN postalcode TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ALTER COLUMN postalcode SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ADD COLUMN country VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ALTER COLUMN country TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ALTER COLUMN country SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.address ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.exchange();
COMMIT;

BEGIN;
ALTER TABLE catalog.exchange ADD COLUMN name VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.exchange ALTER COLUMN name TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.exchange ALTER COLUMN name SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.exchange ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.exchange ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.exchange ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.exchange ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.underlying();
COMMIT;

BEGIN;
ALTER TABLE catalog.underlying ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.underlying ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.underlying ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.underlying ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.optionchain();
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ADD COLUMN exchange_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ALTER COLUMN exchange_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ALTER COLUMN exchange_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ADD COLUMN underlying_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ALTER COLUMN underlying_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ALTER COLUMN underlying_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ADD COLUMN symbol VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ALTER COLUMN symbol TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ALTER COLUMN symbol SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.option();
COMMIT;

BEGIN;
ALTER TABLE catalog.option ADD COLUMN type VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ALTER COLUMN type TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ALTER COLUMN type SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ADD COLUMN expirationdate DATE;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ALTER COLUMN expirationdate TYPE DATE;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ALTER COLUMN expirationdate SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ADD COLUMN strike FLOAT;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ALTER COLUMN strike TYPE FLOAT;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ALTER COLUMN strike SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ADD COLUMN optionchain_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ALTER COLUMN optionchain_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ALTER COLUMN optionchain_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.optionidtype();
COMMIT;

BEGIN;
ALTER TABLE catalog.optionidtype ADD COLUMN name VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionidtype ALTER COLUMN name TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionidtype ALTER COLUMN name SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionidtype ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionidtype ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionidtype ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionidtype ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.optionid();
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ADD COLUMN type_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ALTER COLUMN type_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ALTER COLUMN type_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ADD COLUMN id VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ALTER COLUMN id TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ADD COLUMN id2 SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ALTER COLUMN id2 TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ALTER COLUMN id2 SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ADD COLUMN option_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ALTER COLUMN option_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ALTER COLUMN option_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ADD PRIMARY KEY (id2);
COMMIT;

BEGIN;
CREATE TABLE catalog.propertytype();
COMMIT;

BEGIN;
ALTER TABLE catalog.propertytype ADD COLUMN id VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertytype ALTER COLUMN id TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertytype ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertytype ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.catalog();
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog ADD COLUMN name VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog ALTER COLUMN name TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog ALTER COLUMN name DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog ADD COLUMN root_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog ALTER COLUMN root_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog ALTER COLUMN root_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.item();
COMMIT;

BEGIN;
ALTER TABLE catalog.item ADD COLUMN type VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ALTER COLUMN type TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ALTER COLUMN type SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ADD COLUMN catalog_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ALTER COLUMN catalog_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ALTER COLUMN catalog_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ADD COLUMN containsproducts BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ALTER COLUMN containsproducts TYPE BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ALTER COLUMN containsproducts DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.parentchild();
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ADD COLUMN parent_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ALTER COLUMN parent_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ALTER COLUMN parent_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ADD COLUMN child_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ALTER COLUMN child_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ALTER COLUMN child_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ADD COLUMN index INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ALTER COLUMN index TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ALTER COLUMN index SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.propertygroup();
COMMIT;

BEGIN;
ALTER TABLE catalog.propertygroup ADD COLUMN item_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertygroup ALTER COLUMN item_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertygroup ALTER COLUMN item_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertygroup ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertygroup ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertygroup ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertygroup ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.property();
COMMIT;

BEGIN;
ALTER TABLE catalog.property ADD COLUMN categoryproperty BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ALTER COLUMN categoryproperty TYPE BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ALTER COLUMN categoryproperty SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ADD COLUMN item_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ALTER COLUMN item_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ALTER COLUMN item_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ADD COLUMN type_id VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ALTER COLUMN type_id TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ALTER COLUMN type_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ADD COLUMN ismany BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ALTER COLUMN ismany TYPE BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ALTER COLUMN ismany SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.property_propertygroups();
COMMIT;

BEGIN;
ALTER TABLE catalog.property_propertygroups ADD COLUMN propertygroups_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.property_propertygroups ALTER COLUMN propertygroups_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.property_propertygroups ALTER COLUMN propertygroups_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.property_propertygroups ADD COLUMN properties_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.property_propertygroups ALTER COLUMN properties_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.property_propertygroups ALTER COLUMN properties_id DROP NOT NULL;
COMMIT;

BEGIN;
CREATE TABLE catalog.importsource();
COMMIT;

BEGIN;
ALTER TABLE catalog.importsource ADD COLUMN priority INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.importsource ALTER COLUMN priority TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.importsource ALTER COLUMN priority SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importsource ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importsource ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importsource ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importsource ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.stagingarea();
COMMIT;

BEGIN;
ALTER TABLE catalog.stagingarea ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.stagingarea ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.stagingarea ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.stagingarea ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.enumvalue();
COMMIT;

BEGIN;
ALTER TABLE catalog.enumvalue ADD COLUMN value INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.enumvalue ALTER COLUMN value TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.enumvalue ALTER COLUMN value SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.enumvalue ADD COLUMN property_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.enumvalue ALTER COLUMN property_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.enumvalue ALTER COLUMN property_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.enumvalue ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.enumvalue ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.enumvalue ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.enumvalue ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.label();
COMMIT;

BEGIN;
ALTER TABLE catalog.label ADD COLUMN language VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ALTER COLUMN language TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ALTER COLUMN language DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ADD COLUMN label VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ALTER COLUMN label TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ALTER COLUMN label SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ADD COLUMN property_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ALTER COLUMN property_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ALTER COLUMN property_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ADD COLUMN propertygroup_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ALTER COLUMN propertygroup_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ALTER COLUMN propertygroup_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ADD COLUMN enumvalue_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ALTER COLUMN enumvalue_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ALTER COLUMN enumvalue_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.propertyvalue();
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN property_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN property_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN property_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN language VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN language TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN language DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN stagingarea_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN stagingarea_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN stagingarea_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN importsource_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN importsource_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN importsource_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN stringvalue VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN stringvalue TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN stringvalue DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN integervalue INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN integervalue TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN integervalue DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN enumvalue INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN enumvalue TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN enumvalue DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN realvalue FLOAT;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN realvalue TYPE FLOAT;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN realvalue DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN booleanvalue BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN booleanvalue TYPE BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN booleanvalue DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN moneyvalue FLOAT;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN moneyvalue TYPE FLOAT;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN moneyvalue DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN moneycurrency VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN moneycurrency TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN moneycurrency DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN mediavalue BYTEA;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN mediavalue TYPE BYTEA;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN mediavalue DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN itemvalue_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN itemvalue_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN itemvalue_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN mimetype VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN mimetype TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN mimetype DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN item_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN item_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN item_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD COLUMN outputchannel_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN outputchannel_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ALTER COLUMN outputchannel_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.template();
COMMIT;

BEGIN;
ALTER TABLE catalog.template ADD COLUMN name VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ALTER COLUMN name TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ALTER COLUMN name SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ADD COLUMN language VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ALTER COLUMN language TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ALTER COLUMN language DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ADD COLUMN templatexml VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ALTER COLUMN templatexml TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ALTER COLUMN templatexml SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ADD COLUMN item_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ALTER COLUMN item_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ALTER COLUMN item_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ADD COLUMN shop_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ALTER COLUMN shop_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ALTER COLUMN shop_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.language();
COMMIT;

BEGIN;
ALTER TABLE catalog.language ADD COLUMN name VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.language ALTER COLUMN name TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.language ALTER COLUMN name SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.language ADD COLUMN displayname VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.language ALTER COLUMN displayname TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.language ALTER COLUMN displayname SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.language ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.language ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.language ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.language ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.outputchannel();
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ADD COLUMN type VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN type TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN type SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ADD COLUMN name VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN name TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN name SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ADD COLUMN catalog_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN catalog_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN catalog_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ADD COLUMN defaultlanguage VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN defaultlanguage TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN defaultlanguage DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ADD COLUMN name2 VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN name2 TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN name2 DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ADD COLUMN urlprefix VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN urlprefix TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN urlprefix DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ADD COLUMN defaultlanguage2 VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN defaultlanguage2 TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ALTER COLUMN defaultlanguage2 DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.outputchannel_excludedproperties();
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludedproperties ADD COLUMN excludedproperties_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludedproperties ALTER COLUMN excludedproperties_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludedproperties ALTER COLUMN excludedproperties_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludedproperties ADD COLUMN outputchannel_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludedproperties ALTER COLUMN outputchannel_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludedproperties ALTER COLUMN outputchannel_id DROP NOT NULL;
COMMIT;

BEGIN;
CREATE TABLE catalog.outputchannel_excludeditems();
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludeditems ADD COLUMN excludeditems_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludeditems ALTER COLUMN excludeditems_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludeditems ALTER COLUMN excludeditems_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludeditems ADD COLUMN outputchannel_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludeditems ALTER COLUMN outputchannel_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludeditems ALTER COLUMN outputchannel_id DROP NOT NULL;
COMMIT;

BEGIN;
CREATE TABLE catalog.query();
COMMIT;

BEGIN;
ALTER TABLE catalog.query ADD COLUMN type VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN type TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN type SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ADD COLUMN stringvalue VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN stringvalue TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN stringvalue DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ADD COLUMN category_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN category_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN category_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ADD COLUMN stringvalue2 VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN stringvalue2 TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN stringvalue2 DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ADD COLUMN shop_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN shop_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN shop_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ADD COLUMN shop_id2 INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN shop_id2 TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ALTER COLUMN shop_id2 DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.importdefinition();
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ADD COLUMN type VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN type TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN type SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ADD COLUMN name VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN name TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN name SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ADD COLUMN importurl VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN importurl TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN importurl DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ADD COLUMN importfilebasename VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN importfilebasename TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN importfilebasename DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ADD COLUMN importfilepattern VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN importfilepattern TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN importfilepattern DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ADD COLUMN importsourcename VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN importsourcename TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN importsourcename SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ADD COLUMN importsourcenameappendfilename BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN importsourcenameappendfilename TYPE BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN importsourcenameappendfilename SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ADD COLUMN headerline BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN headerline TYPE BOOLEAN;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ALTER COLUMN headerline DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importdefinition ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.importcategory();
COMMIT;

BEGIN;
ALTER TABLE catalog.importcategory ADD COLUMN importdefinition_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.importcategory ALTER COLUMN importdefinition_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.importcategory ALTER COLUMN importdefinition_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importcategory ADD COLUMN expression VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importcategory ALTER COLUMN expression TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importcategory ALTER COLUMN expression SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importcategory ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importcategory ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importcategory ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importcategory ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.importproperty();
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ADD COLUMN importdefinition_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ALTER COLUMN importdefinition_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ALTER COLUMN importdefinition_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ADD COLUMN property_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ALTER COLUMN property_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ALTER COLUMN property_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ADD COLUMN expression VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ALTER COLUMN expression TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ALTER COLUMN expression SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.shop_excludedproperties();
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludedproperties ADD COLUMN excludedproperties_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludedproperties ALTER COLUMN excludedproperties_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludedproperties ALTER COLUMN excludedproperties_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludedproperties ADD COLUMN shop_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludedproperties ALTER COLUMN shop_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludedproperties ALTER COLUMN shop_id DROP NOT NULL;
COMMIT;

BEGIN;
CREATE TABLE catalog.shop_excludeditems();
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludeditems ADD COLUMN excludeditems_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludeditems ALTER COLUMN excludeditems_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludeditems ALTER COLUMN excludeditems_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludeditems ADD COLUMN shop_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludeditems ALTER COLUMN shop_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludeditems ALTER COLUMN shop_id DROP NOT NULL;
COMMIT;

BEGIN;
CREATE TABLE catalog.navigation();
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ADD COLUMN category_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ALTER COLUMN category_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ALTER COLUMN category_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ADD COLUMN index INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ALTER COLUMN index TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ALTER COLUMN index SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ADD COLUMN parentshop_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ALTER COLUMN parentshop_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ALTER COLUMN parentshop_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ADD COLUMN parentnavigation_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ALTER COLUMN parentnavigation_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ALTER COLUMN parentnavigation_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.promotion();
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ADD COLUMN type VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN type TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN type SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ADD COLUMN startdate DATE;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN startdate TYPE DATE;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN startdate SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ADD COLUMN enddate DATE;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN enddate TYPE DATE;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN enddate SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ADD COLUMN shop_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN shop_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN shop_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ADD COLUMN product_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN product_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN product_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ADD COLUMN price FLOAT;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN price TYPE FLOAT;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN price DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ADD COLUMN pricecurrency VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN pricecurrency TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN pricecurrency DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ADD COLUMN volumediscount INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN volumediscount TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ALTER COLUMN volumediscount DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.promotion_templates();
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion_templates ADD COLUMN templates_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion_templates ALTER COLUMN templates_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion_templates ALTER COLUMN templates_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion_templates ADD COLUMN promotion_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion_templates ALTER COLUMN promotion_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion_templates ALTER COLUMN promotion_id DROP NOT NULL;
COMMIT;

BEGIN;
CREATE TABLE catalog.berth();
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ADD COLUMN name VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ALTER COLUMN name TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ALTER COLUMN name SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ADD COLUMN description2_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ALTER COLUMN description2_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ALTER COLUMN description2_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ADD COLUMN profile_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ALTER COLUMN profile_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ALTER COLUMN profile_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.label2();
COMMIT;

BEGIN;
ALTER TABLE catalog.label2 ADD COLUMN language VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.label2 ALTER COLUMN language TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.label2 ALTER COLUMN language DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label2 ADD COLUMN label VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.label2 ALTER COLUMN label TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.label2 ALTER COLUMN label SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label2 ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label2 ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label2 ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.label2 ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.orderstatus();
COMMIT;

BEGIN;
ALTER TABLE catalog.orderstatus ADD COLUMN id VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderstatus ALTER COLUMN id TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderstatus ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderstatus ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.order();
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD COLUMN shop_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN shop_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN shop_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD COLUMN orderdate DATE;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN orderdate TYPE DATE;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN orderdate SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD COLUMN deliveryaddress_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN deliveryaddress_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN deliveryaddress_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD COLUMN user_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN user_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN user_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD COLUMN status_id VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN status_id TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN status_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD COLUMN amountpaid FLOAT;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN amountpaid TYPE FLOAT;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN amountpaid SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD COLUMN transport_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN transport_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ALTER COLUMN transport_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.productorder();
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ADD COLUMN product_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN product_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN product_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ADD COLUMN promotion_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN promotion_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN promotion_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ADD COLUMN volume INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN volume TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN volume SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ADD COLUMN price FLOAT;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN price TYPE FLOAT;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN price SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ADD COLUMN pricecurrency VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN pricecurrency TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN pricecurrency SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ADD COLUMN order_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN order_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN order_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.transport();
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ADD COLUMN desciption VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ALTER COLUMN desciption TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ALTER COLUMN desciption SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ADD COLUMN transportcompany VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ALTER COLUMN transportcompany TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ALTER COLUMN transportcompany SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ADD COLUMN deliverytime INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ALTER COLUMN deliverytime TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ALTER COLUMN deliverytime SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.transport ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.orderhistory();
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ADD COLUMN user_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ALTER COLUMN user_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ALTER COLUMN user_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ADD COLUMN newstatus_id VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ALTER COLUMN newstatus_id TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ALTER COLUMN newstatus_id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ADD COLUMN comment VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ALTER COLUMN comment TYPE VARCHAR;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ALTER COLUMN comment SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ADD COLUMN date DATE;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ALTER COLUMN date TYPE DATE;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ALTER COLUMN date SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ADD COLUMN id SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ALTER COLUMN id TYPE SERIAL;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ALTER COLUMN id SET NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ADD COLUMN order_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ALTER COLUMN order_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ALTER COLUMN order_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ADD PRIMARY KEY (id);
COMMIT;

BEGIN;
CREATE TABLE catalog.catalog_templates();
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_templates ADD COLUMN templates_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_templates ALTER COLUMN templates_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_templates ALTER COLUMN templates_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_templates ADD COLUMN catalog_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_templates ALTER COLUMN catalog_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_templates ALTER COLUMN catalog_id DROP NOT NULL;
COMMIT;

BEGIN;
CREATE TABLE catalog.catalog_languages();
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_languages ADD COLUMN languages_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_languages ALTER COLUMN languages_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_languages ALTER COLUMN languages_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_languages ADD COLUMN catalog_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_languages ALTER COLUMN catalog_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_languages ALTER COLUMN catalog_id DROP NOT NULL;
COMMIT;

BEGIN;
CREATE TABLE catalog.product_templates();
COMMIT;

BEGIN;
ALTER TABLE catalog.product_templates ADD COLUMN templates_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.product_templates ALTER COLUMN templates_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.product_templates ALTER COLUMN templates_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.product_templates ADD COLUMN product_id INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.product_templates ALTER COLUMN product_id TYPE INTEGER;
COMMIT;

BEGIN;
ALTER TABLE catalog.product_templates ALTER COLUMN product_id DROP NOT NULL;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD CONSTRAINT fk_address FOREIGN KEY (address_id) REFERENCES catalog.address (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD CONSTRAINT fk_shippingaddress FOREIGN KEY (shippingaddress_id) REFERENCES catalog.address (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD CONSTRAINT fk_deliveryaddress FOREIGN KEY (deliveryaddress_id) REFERENCES catalog.address (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.party ADD CONSTRAINT fk_billingaddress FOREIGN KEY (billingaddress_id) REFERENCES catalog.address (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.user ADD CONSTRAINT fk_party FOREIGN KEY (party_id) REFERENCES catalog.party (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ADD CONSTRAINT fk_exchange FOREIGN KEY (exchange_id) REFERENCES catalog.exchange (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionchain ADD CONSTRAINT fk_underlying FOREIGN KEY (underlying_id) REFERENCES catalog.underlying (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.option ADD CONSTRAINT fk_optionchain FOREIGN KEY (optionchain_id) REFERENCES catalog.optionchain (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ADD CONSTRAINT fk_type FOREIGN KEY (type_id) REFERENCES catalog.optionidtype (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.optionid ADD CONSTRAINT fk_option FOREIGN KEY (option_id) REFERENCES catalog.option (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog ADD CONSTRAINT fk_root FOREIGN KEY (root_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.item ADD CONSTRAINT fk_catalog FOREIGN KEY (catalog_id) REFERENCES catalog.catalog (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ADD CONSTRAINT fk_parent FOREIGN KEY (parent_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.parentchild ADD CONSTRAINT fk_child FOREIGN KEY (child_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertygroup ADD CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ADD CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.property ADD CONSTRAINT fk_propertytype FOREIGN KEY (type_id) REFERENCES catalog.propertytype (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.property_propertygroups ADD CONSTRAINT fk_propertygroup FOREIGN KEY (propertygroups_id) REFERENCES catalog.propertygroup (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.property_propertygroups ADD CONSTRAINT fk_property FOREIGN KEY (properties_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.enumvalue ADD CONSTRAINT fk_property FOREIGN KEY (property_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ADD CONSTRAINT fk_property FOREIGN KEY (property_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ADD CONSTRAINT fk_propertygroup FOREIGN KEY (propertygroup_id) REFERENCES catalog.propertygroup (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.label ADD CONSTRAINT fk_enumvalue FOREIGN KEY (enumvalue_id) REFERENCES catalog.enumvalue (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD CONSTRAINT fk_property FOREIGN KEY (property_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD CONSTRAINT fk_stagingarea FOREIGN KEY (stagingarea_id) REFERENCES catalog.stagingarea (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD CONSTRAINT fk_importsource FOREIGN KEY (importsource_id) REFERENCES catalog.importsource (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD CONSTRAINT fk_itemvalue FOREIGN KEY (itemvalue_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.propertyvalue ADD CONSTRAINT fk_outputchannel FOREIGN KEY (outputchannel_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ADD CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.template ADD CONSTRAINT fk_outputchannel FOREIGN KEY (shop_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel ADD CONSTRAINT fk_catalog FOREIGN KEY (catalog_id) REFERENCES catalog.catalog (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludedproperties ADD CONSTRAINT fk_property FOREIGN KEY (excludedproperties_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludedproperties ADD CONSTRAINT fk_outputchannel FOREIGN KEY (outputchannel_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludeditems ADD CONSTRAINT fk_item FOREIGN KEY (excludeditems_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.outputchannel_excludeditems ADD CONSTRAINT fk_outputchannel FOREIGN KEY (outputchannel_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ADD CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ADD CONSTRAINT fk_shop FOREIGN KEY (shop_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.query ADD CONSTRAINT fk_shop2 FOREIGN KEY (shop_id2) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.importcategory ADD CONSTRAINT fk_importdefinition FOREIGN KEY (importdefinition_id) REFERENCES catalog.importdefinition (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ADD CONSTRAINT fk_importdefinition FOREIGN KEY (importdefinition_id) REFERENCES catalog.importdefinition (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.importproperty ADD CONSTRAINT fk_property FOREIGN KEY (property_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludedproperties ADD CONSTRAINT fk_property FOREIGN KEY (excludedproperties_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludedproperties ADD CONSTRAINT fk_outputchannel FOREIGN KEY (shop_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludeditems ADD CONSTRAINT fk_item FOREIGN KEY (excludeditems_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.shop_excludeditems ADD CONSTRAINT fk_outputchannel FOREIGN KEY (shop_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ADD CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ADD CONSTRAINT fk_parentshop FOREIGN KEY (parentshop_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.navigation ADD CONSTRAINT fk_parentnavigation FOREIGN KEY (parentnavigation_id) REFERENCES catalog.navigation (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ADD CONSTRAINT fk_shop FOREIGN KEY (shop_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion ADD CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion_templates ADD CONSTRAINT fk_template FOREIGN KEY (templates_id) REFERENCES catalog.template (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.promotion_templates ADD CONSTRAINT fk_promotion FOREIGN KEY (promotion_id) REFERENCES catalog.promotion (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ADD CONSTRAINT fk_description2 FOREIGN KEY (description2_id) REFERENCES catalog.label2 (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.berth ADD CONSTRAINT fk_profile FOREIGN KEY (profile_id) REFERENCES catalog.label2 (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD CONSTRAINT fk_shop FOREIGN KEY (shop_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD CONSTRAINT fk_deliveryaddress FOREIGN KEY (deliveryaddress_id) REFERENCES catalog.address (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES catalog.user (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD CONSTRAINT fk_orderstatus FOREIGN KEY (status_id) REFERENCES catalog.orderstatus (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.order ADD CONSTRAINT fk_transport FOREIGN KEY (transport_id) REFERENCES catalog.transport (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ADD CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ADD CONSTRAINT fk_promotion FOREIGN KEY (promotion_id) REFERENCES catalog.promotion (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.productorder ADD CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES catalog.order (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES catalog.user (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ADD CONSTRAINT fk_orderstatus FOREIGN KEY (newstatus_id) REFERENCES catalog.orderstatus (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.orderhistory ADD CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES catalog.order (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_templates ADD CONSTRAINT fk_template FOREIGN KEY (templates_id) REFERENCES catalog.template (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_templates ADD CONSTRAINT fk_catalog FOREIGN KEY (catalog_id) REFERENCES catalog.catalog (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_languages ADD CONSTRAINT fk_language FOREIGN KEY (languages_id) REFERENCES catalog.language (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.catalog_languages ADD CONSTRAINT fk_catalog FOREIGN KEY (catalog_id) REFERENCES catalog.catalog (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.product_templates ADD CONSTRAINT fk_template FOREIGN KEY (templates_id) REFERENCES catalog.template (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;

BEGIN;
ALTER TABLE catalog.product_templates ADD CONSTRAINT fk_item FOREIGN KEY (product_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
COMMIT;


-- Update initial content in propertytype
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('String');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Integer');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Real');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Boolean');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Money');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Enum');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Media');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('FormattedText');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Length');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Mass');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Time');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('ElectricCurrent');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Temperature');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('LuminousIntensity');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('AmountOfSubstance');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Frequency');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Angle');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Energy');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Power');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Voltage');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Area');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Volume');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Velocity');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Acceleration');
COMMIT;
BEGIN;
INSERT INTO catalog.propertytype (id) VALUES ('Item');
COMMIT;

-- Update initial content in orderstatus
BEGIN;
INSERT INTO catalog.orderstatus (id) VALUES ('InShoppingCart');
COMMIT;
BEGIN;
INSERT INTO catalog.orderstatus (id) VALUES ('PendingPayment');
COMMIT;
BEGIN;
INSERT INTO catalog.orderstatus (id) VALUES ('ReceivedPayment');
COMMIT;
BEGIN;
INSERT INTO catalog.orderstatus (id) VALUES ('Processing');
COMMIT;
BEGIN;
INSERT INTO catalog.orderstatus (id) VALUES ('Shipped');
COMMIT;
BEGIN;
INSERT INTO catalog.orderstatus (id) VALUES ('OnHold');
COMMIT;
BEGIN;
INSERT INTO catalog.orderstatus (id) VALUES ('Complete');
COMMIT;
BEGIN;
INSERT INTO catalog.orderstatus (id) VALUES ('Closed');
COMMIT;
BEGIN;
INSERT INTO catalog.orderstatus (id) VALUES ('Canceled');
COMMIT;

