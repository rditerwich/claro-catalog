CREATE SCHEMA catalog;

-- Create Tables

CREATE TABLE catalog.party (
    name                 VARCHAR NOT NULL,
    phonenumber          VARCHAR NOT NULL,
    website              VARCHAR,
    billingname          VARCHAR,
    id                   SERIAL NOT NULL,
    address_id           INTEGER NOT NULL,
    shippingaddress_id   INTEGER,
    deliveryaddress_id   INTEGER,
    billingaddress_id    INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.user (
    email                VARCHAR NOT NULL,
    password             VARCHAR NOT NULL,
    party_id             INTEGER NOT NULL,
    iscataloguser        BOOLEAN NOT NULL,
    uilanguage           VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.emailconfirmation (
    email                VARCHAR NOT NULL,
    confirmationkey      VARCHAR NOT NULL,
    expirationtime       BIGINT NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.address (
    address1             VARCHAR NOT NULL,
    address2             VARCHAR,
    town                 VARCHAR NOT NULL,
    postalcode           VARCHAR NOT NULL,
    country              VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.importrules (
    relativeurl          VARCHAR,
    languageexpression   VARCHAR,
    defaultcurrency      VARCHAR,
    id                   SERIAL NOT NULL,
    fileformat_id        INTEGER,
    tabularfileformat_id INTEGER,
    xmlfileformat_id     INTEGER,
    importsource_id      INTEGER NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.importproducts (
    outputchannelexpression VARCHAR NOT NULL,
    rules_id             INTEGER NOT NULL,
    id                   SERIAL NOT NULL,
    matchproperty_id     INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.importfileformat (
    type                 VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    headerline           BOOLEAN,
    charset              VARCHAR,
    separatorchars       VARCHAR,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.importcategory (
    importproducts_id    INTEGER NOT NULL,
    categoryexpression   VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.importproperty (
    importproducts_id    INTEGER NOT NULL,
    valueexpression      VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    property_id          INTEGER NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.propertytype (
    id                   VARCHAR NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.catalog (
    name                 VARCHAR,
    id                   SERIAL NOT NULL,
    root_id              INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.item (
    type                 VARCHAR NOT NULL,
    catalog_id           INTEGER NOT NULL,
    id                   SERIAL NOT NULL,
    containsproducts     BOOLEAN DEFAULT true,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.parentchild (
    parent_id            INTEGER NOT NULL,
    child_id             INTEGER NOT NULL,
    index                INTEGER NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.propertygroup (
    catalog_id           INTEGER NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.propertygroupassignment (
    propertygroup_id     INTEGER NOT NULL,
    category_id          INTEGER NOT NULL,
    id                   SERIAL NOT NULL,
    property_id          INTEGER NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.property (
    categoryproperty     BOOLEAN NOT NULL DEFAULT false,
    item_id              INTEGER,
    type_id              VARCHAR NOT NULL,
    ismany               BOOLEAN NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.stagingarea (
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.enumvalue (
    value                INTEGER NOT NULL,
    property_id          INTEGER NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.label (
    language             VARCHAR,
    label                VARCHAR NOT NULL,
    property_id          INTEGER,
    propertygroup_id     INTEGER,
    enumvalue_id         INTEGER,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.propertyvalue (
    property_id          INTEGER NOT NULL,
    language             VARCHAR,
    stagingarea_id       INTEGER,
    stringvalue          VARCHAR,
    integervalue         INTEGER,
    enumvalue            INTEGER,
    realvalue            FLOAT,
    booleanvalue         BOOLEAN,
    moneyvalue           FLOAT,
    moneycurrency        VARCHAR,
    mediavalue           BYTEA,
    itemvalue_id         INTEGER,
    mimetype             VARCHAR,
    item_id              INTEGER NOT NULL,
    id                   SERIAL NOT NULL,
    outputchannel_id     INTEGER,
    source_id            INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.template (
    name                 VARCHAR NOT NULL,
    language             VARCHAR,
    templatexml          VARCHAR NOT NULL,
    item_id              INTEGER,
    id                   SERIAL NOT NULL,
    shop_id              INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.language (
    name                 VARCHAR NOT NULL,
    displayname          VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.source (
    type                 VARCHAR NOT NULL,
    priority             INTEGER,
    id                   SERIAL NOT NULL,
    name                 VARCHAR,
    importurl            VARCHAR,
    sequentialurl        BOOLEAN,
    orderedurl           BOOLEAN,
    incremental          BOOLEAN,
    lastimportedurl      VARCHAR,
    multifileimport      BOOLEAN DEFAULT false,
    job_id               INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.outputchannel (
    type                 VARCHAR NOT NULL,
    name                 VARCHAR NOT NULL,
    catalog_id           INTEGER NOT NULL,
    defaultlanguage      VARCHAR,
    id                   SERIAL NOT NULL,
    urlprefix            VARCHAR,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.outputchannel_excludedproperties (
    excludedproperties_id INTEGER,
    outputchannel_id     INTEGER
     
);

CREATE TABLE catalog.outputchannel_excludeditems (
    excludeditems_id     INTEGER,
    outputchannel_id     INTEGER
     
);

CREATE TABLE catalog.exchange (
    name                 VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.underlying (
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.optionchain (
    exchange_id          INTEGER NOT NULL,
    underlying_id        INTEGER NOT NULL,
    symbol               VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.option (
    type                 VARCHAR NOT NULL,
    expirationdate       DATE NOT NULL,
    strike               FLOAT NOT NULL,
    id                   SERIAL NOT NULL,
    optionchain_id       INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.optionidtype (
    name                 VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.optionid (
    type_id              INTEGER NOT NULL,
    id                   VARCHAR NOT NULL,
    id2                  SERIAL NOT NULL,
    option_id            INTEGER NOT NULL,
    PRIMARY KEY (id2) 
);

CREATE TABLE catalog.query (
    type                 VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    stringvalue          VARCHAR,
    category_id          INTEGER,
    stringvalue2         VARCHAR,
    shop_id              INTEGER,
    shop_id2             INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.orderstatus (
    id                   VARCHAR NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.order (
    orderdate            DATE NOT NULL,
    deliveryaddress_id   INTEGER,
    user_id              INTEGER NOT NULL,
    status_id            VARCHAR NOT NULL,
    amountpaid           FLOAT NOT NULL,
    id                   SERIAL NOT NULL,
    shop_id              INTEGER NOT NULL,
    transport_id         INTEGER NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.productorder (
    product_id           INTEGER NOT NULL,
    volume               INTEGER NOT NULL,
    price                FLOAT NOT NULL,
    pricecurrency        VARCHAR NOT NULL,
    order_id             INTEGER NOT NULL,
    id                   SERIAL NOT NULL,
    promotion_id         INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.transport (
    desciption           VARCHAR NOT NULL,
    transportcompany     VARCHAR NOT NULL,
    deliverytime         INTEGER NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.orderhistory (
    user_id              INTEGER NOT NULL,
    newstatus_id         VARCHAR NOT NULL,
    comment              VARCHAR NOT NULL,
    date                 DATE NOT NULL,
    id                   SERIAL NOT NULL,
    order_id             INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.navigation (
    category_id          INTEGER,
    index                INTEGER NOT NULL,
    parentshop_id        INTEGER,
    id                   SERIAL NOT NULL,
    parentnavigation_id  INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.promotion (
    type                 VARCHAR NOT NULL,
    startdate            DATE NOT NULL,
    enddate              DATE NOT NULL,
    shop_id              INTEGER NOT NULL,
    id                   SERIAL NOT NULL,
    product_id           INTEGER,
    price                FLOAT,
    pricecurrency        VARCHAR,
    volumediscount       INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.promotion_templates (
    templates_id         INTEGER,
    promotion_id         INTEGER
     
);

CREATE TABLE catalog.berth (
    name                 VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    description2_id      INTEGER NOT NULL,
    profile_id           INTEGER,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.label2 (
    language             VARCHAR,
    label                VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.frequency (
    id                   VARCHAR NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.job (
    name                 VARCHAR NOT NULL,
    firstrun             TIMESTAMP,
    runfrequency_id      VARCHAR NOT NULL,
    healthperc           INTEGER,
    lastsuccess          BOOLEAN,
    lasttime             TIMESTAMP,
    id                   SERIAL NOT NULL,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.jobresult (
    type                 VARCHAR NOT NULL,
    job_id               INTEGER NOT NULL,
    success              BOOLEAN NOT NULL,
    starttime            TIMESTAMP NOT NULL,
    endtime              TIMESTAMP NOT NULL,
    log                  VARCHAR NOT NULL,
    id                   SERIAL NOT NULL,
    importsource_id      INTEGER,
    url                  VARCHAR,
    PRIMARY KEY (id) 
);

CREATE TABLE catalog.catalog_templates (
    templates_id         INTEGER,
    catalog_id           INTEGER
     
);

CREATE TABLE catalog.catalog_languages (
    languages_id         INTEGER,
    catalog_id           INTEGER
     
);

CREATE TABLE catalog.product_templates (
    templates_id         INTEGER,
    product_id           INTEGER
     
);

-- Create Foreign key constraints

ALTER TABLE catalog.party ADD CONSTRAINT fk_address FOREIGN KEY (address_id) REFERENCES catalog.address (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.party ADD CONSTRAINT fk_shippingaddress FOREIGN KEY (shippingaddress_id) REFERENCES catalog.address (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.party ADD CONSTRAINT fk_deliveryaddress FOREIGN KEY (deliveryaddress_id) REFERENCES catalog.address (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.party ADD CONSTRAINT fk_billingaddress FOREIGN KEY (billingaddress_id) REFERENCES catalog.address (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.user ADD CONSTRAINT fk_party FOREIGN KEY (party_id) REFERENCES catalog.party (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.importrules ADD CONSTRAINT fk_fileformat FOREIGN KEY (fileformat_id) REFERENCES catalog.importfileformat (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.importrules ADD CONSTRAINT fk_tabularfileformat FOREIGN KEY (tabularfileformat_id) REFERENCES catalog.importfileformat (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.importrules ADD CONSTRAINT fk_xmlfileformat FOREIGN KEY (xmlfileformat_id) REFERENCES catalog.importfileformat (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.importrules ADD CONSTRAINT fk_importsource FOREIGN KEY (importsource_id) REFERENCES catalog.source (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.importproducts ADD CONSTRAINT fk_rules FOREIGN KEY (rules_id) REFERENCES catalog.importrules (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.importproducts ADD CONSTRAINT fk_matchproperty FOREIGN KEY (matchproperty_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.importcategory ADD CONSTRAINT fk_importproducts FOREIGN KEY (importproducts_id) REFERENCES catalog.importproducts (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.importproperty ADD CONSTRAINT fk_importproducts FOREIGN KEY (importproducts_id) REFERENCES catalog.importproducts (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.importproperty ADD CONSTRAINT fk_property FOREIGN KEY (property_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.catalog ADD CONSTRAINT fk_root FOREIGN KEY (root_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.item ADD CONSTRAINT fk_catalog FOREIGN KEY (catalog_id) REFERENCES catalog.catalog (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.parentchild ADD CONSTRAINT fk_parent FOREIGN KEY (parent_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.parentchild ADD CONSTRAINT fk_child FOREIGN KEY (child_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.propertygroup ADD CONSTRAINT fk_catalog FOREIGN KEY (catalog_id) REFERENCES catalog.catalog (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.propertygroupassignment ADD CONSTRAINT fk_propertygroup FOREIGN KEY (propertygroup_id) REFERENCES catalog.propertygroup (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.propertygroupassignment ADD CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.propertygroupassignment ADD CONSTRAINT fk_property FOREIGN KEY (property_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.property ADD CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.property ADD CONSTRAINT fk_propertytype FOREIGN KEY (type_id) REFERENCES catalog.propertytype (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.enumvalue ADD CONSTRAINT fk_property FOREIGN KEY (property_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.label ADD CONSTRAINT fk_property FOREIGN KEY (property_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.label ADD CONSTRAINT fk_propertygroup FOREIGN KEY (propertygroup_id) REFERENCES catalog.propertygroup (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.label ADD CONSTRAINT fk_enumvalue FOREIGN KEY (enumvalue_id) REFERENCES catalog.enumvalue (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.propertyvalue ADD CONSTRAINT fk_property FOREIGN KEY (property_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.propertyvalue ADD CONSTRAINT fk_stagingarea FOREIGN KEY (stagingarea_id) REFERENCES catalog.stagingarea (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.propertyvalue ADD CONSTRAINT fk_itemvalue FOREIGN KEY (itemvalue_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.propertyvalue ADD CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.propertyvalue ADD CONSTRAINT fk_outputchannel FOREIGN KEY (outputchannel_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.propertyvalue ADD CONSTRAINT fk_source FOREIGN KEY (source_id) REFERENCES catalog.source (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.template ADD CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.template ADD CONSTRAINT fk_outputchannel FOREIGN KEY (shop_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.source ADD CONSTRAINT fk_job FOREIGN KEY (job_id) REFERENCES catalog.job (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.outputchannel ADD CONSTRAINT fk_catalog FOREIGN KEY (catalog_id) REFERENCES catalog.catalog (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.outputchannel_excludedproperties ADD CONSTRAINT fk_property FOREIGN KEY (excludedproperties_id) REFERENCES catalog.property (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.outputchannel_excludedproperties ADD CONSTRAINT fk_outputchannel FOREIGN KEY (outputchannel_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.outputchannel_excludeditems ADD CONSTRAINT fk_item FOREIGN KEY (excludeditems_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.outputchannel_excludeditems ADD CONSTRAINT fk_outputchannel FOREIGN KEY (outputchannel_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.optionchain ADD CONSTRAINT fk_exchange FOREIGN KEY (exchange_id) REFERENCES catalog.exchange (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.optionchain ADD CONSTRAINT fk_underlying FOREIGN KEY (underlying_id) REFERENCES catalog.underlying (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.option ADD CONSTRAINT fk_optionchain FOREIGN KEY (optionchain_id) REFERENCES catalog.optionchain (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.optionid ADD CONSTRAINT fk_type FOREIGN KEY (type_id) REFERENCES catalog.optionidtype (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.optionid ADD CONSTRAINT fk_option FOREIGN KEY (option_id) REFERENCES catalog.option (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.query ADD CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.query ADD CONSTRAINT fk_shop FOREIGN KEY (shop_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.query ADD CONSTRAINT fk_shop2 FOREIGN KEY (shop_id2) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.order ADD CONSTRAINT fk_deliveryaddress FOREIGN KEY (deliveryaddress_id) REFERENCES catalog.address (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.order ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES catalog.user (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.order ADD CONSTRAINT fk_orderstatus FOREIGN KEY (status_id) REFERENCES catalog.orderstatus (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.order ADD CONSTRAINT fk_shop FOREIGN KEY (shop_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.order ADD CONSTRAINT fk_transport FOREIGN KEY (transport_id) REFERENCES catalog.transport (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.productorder ADD CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.productorder ADD CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES catalog.order (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.productorder ADD CONSTRAINT fk_promotion FOREIGN KEY (promotion_id) REFERENCES catalog.promotion (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.orderhistory ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES catalog.user (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.orderhistory ADD CONSTRAINT fk_orderstatus FOREIGN KEY (newstatus_id) REFERENCES catalog.orderstatus (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.orderhistory ADD CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES catalog.order (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.navigation ADD CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.navigation ADD CONSTRAINT fk_parentshop FOREIGN KEY (parentshop_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.navigation ADD CONSTRAINT fk_parentnavigation FOREIGN KEY (parentnavigation_id) REFERENCES catalog.navigation (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.promotion ADD CONSTRAINT fk_shop FOREIGN KEY (shop_id) REFERENCES catalog.outputchannel (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.promotion ADD CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.promotion_templates ADD CONSTRAINT fk_template FOREIGN KEY (templates_id) REFERENCES catalog.template (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.promotion_templates ADD CONSTRAINT fk_promotion FOREIGN KEY (promotion_id) REFERENCES catalog.promotion (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.berth ADD CONSTRAINT fk_description2 FOREIGN KEY (description2_id) REFERENCES catalog.label2 (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.berth ADD CONSTRAINT fk_profile FOREIGN KEY (profile_id) REFERENCES catalog.label2 (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.job ADD CONSTRAINT fk_frequency FOREIGN KEY (runfrequency_id) REFERENCES catalog.frequency (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.jobresult ADD CONSTRAINT fk_job FOREIGN KEY (job_id) REFERENCES catalog.job (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.jobresult ADD CONSTRAINT fk_importsource FOREIGN KEY (importsource_id) REFERENCES catalog.source (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.catalog_templates ADD CONSTRAINT fk_template FOREIGN KEY (templates_id) REFERENCES catalog.template (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.catalog_templates ADD CONSTRAINT fk_catalog FOREIGN KEY (catalog_id) REFERENCES catalog.catalog (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.catalog_languages ADD CONSTRAINT fk_language FOREIGN KEY (languages_id) REFERENCES catalog.language (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.catalog_languages ADD CONSTRAINT fk_catalog FOREIGN KEY (catalog_id) REFERENCES catalog.catalog (id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE catalog.product_templates ADD CONSTRAINT fk_template FOREIGN KEY (templates_id) REFERENCES catalog.template (id) DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE catalog.product_templates ADD CONSTRAINT fk_item FOREIGN KEY (product_id) REFERENCES catalog.item (id) DEFERRABLE INITIALLY DEFERRED;


-- Insert initial content in propertytype
INSERT INTO catalog.propertytype (id) VALUES ('String');
INSERT INTO catalog.propertytype (id) VALUES ('Integer');
INSERT INTO catalog.propertytype (id) VALUES ('Real');
INSERT INTO catalog.propertytype (id) VALUES ('Boolean');
INSERT INTO catalog.propertytype (id) VALUES ('Money');
INSERT INTO catalog.propertytype (id) VALUES ('Enum');
INSERT INTO catalog.propertytype (id) VALUES ('Media');
INSERT INTO catalog.propertytype (id) VALUES ('FormattedText');
INSERT INTO catalog.propertytype (id) VALUES ('Length');
INSERT INTO catalog.propertytype (id) VALUES ('Mass');
INSERT INTO catalog.propertytype (id) VALUES ('Time');
INSERT INTO catalog.propertytype (id) VALUES ('ElectricCurrent');
INSERT INTO catalog.propertytype (id) VALUES ('Temperature');
INSERT INTO catalog.propertytype (id) VALUES ('LuminousIntensity');
INSERT INTO catalog.propertytype (id) VALUES ('AmountOfSubstance');
INSERT INTO catalog.propertytype (id) VALUES ('Frequency');
INSERT INTO catalog.propertytype (id) VALUES ('Angle');
INSERT INTO catalog.propertytype (id) VALUES ('Energy');
INSERT INTO catalog.propertytype (id) VALUES ('Power');
INSERT INTO catalog.propertytype (id) VALUES ('Voltage');
INSERT INTO catalog.propertytype (id) VALUES ('Area');
INSERT INTO catalog.propertytype (id) VALUES ('Volume');
INSERT INTO catalog.propertytype (id) VALUES ('Velocity');
INSERT INTO catalog.propertytype (id) VALUES ('Acceleration');
INSERT INTO catalog.propertytype (id) VALUES ('Item');

-- Insert initial content in orderstatus
INSERT INTO catalog.orderstatus (id) VALUES ('InShoppingCart');
INSERT INTO catalog.orderstatus (id) VALUES ('PendingPayment');
INSERT INTO catalog.orderstatus (id) VALUES ('ReceivedPayment');
INSERT INTO catalog.orderstatus (id) VALUES ('Processing');
INSERT INTO catalog.orderstatus (id) VALUES ('Shipped');
INSERT INTO catalog.orderstatus (id) VALUES ('OnHold');
INSERT INTO catalog.orderstatus (id) VALUES ('Complete');
INSERT INTO catalog.orderstatus (id) VALUES ('Closed');
INSERT INTO catalog.orderstatus (id) VALUES ('Canceled');

-- Insert initial content in frequency
INSERT INTO catalog.frequency (id) VALUES ('never');
INSERT INTO catalog.frequency (id) VALUES ('hourly');
INSERT INTO catalog.frequency (id) VALUES ('daily');
INSERT INTO catalog.frequency (id) VALUES ('weekDays');
INSERT INTO catalog.frequency (id) VALUES ('weekly');
INSERT INTO catalog.frequency (id) VALUES ('monthly');
INSERT INTO catalog.frequency (id) VALUES ('yearly');

