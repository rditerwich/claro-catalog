package claro.jpa.catalog;

import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.StagingArea;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2013-02-22T11:24:28")
@StaticMetamodel(StagingStatus.class)
public class StagingStatus_ { 

    public static volatile SingularAttribute<StagingStatus, Long> id;
    public static volatile SingularAttribute<StagingStatus, Integer> updateSequenceNr;
    public static volatile SingularAttribute<StagingStatus, Catalog> catalog;
    public static volatile SingularAttribute<StagingStatus, StagingArea> stagingArea;

}