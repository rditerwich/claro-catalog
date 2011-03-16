package claro.jpa.media;

import claro.jpa.catalog.Catalog;
import claro.jpa.media.MediaContent;
import claro.jpa.media.MediaTag;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-03-15T16:43:49")
@StaticMetamodel(Media.class)
public class Media_ { 

    public static volatile ListAttribute<Media, MediaTag> tags;
    public static volatile SingularAttribute<Media, MediaContent> content;
    public static volatile SingularAttribute<Media, Long> id;
    public static volatile SingularAttribute<Media, Catalog> catalog;
    public static volatile SingularAttribute<Media, String> name;

}