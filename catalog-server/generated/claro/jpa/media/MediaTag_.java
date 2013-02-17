package claro.jpa.media;

import claro.jpa.media.Media;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-03-15T16:43:49")
@StaticMetamodel(MediaTag.class)
public class MediaTag_ { 

    public static volatile SingularAttribute<MediaTag, Long> id;
    public static volatile SingularAttribute<MediaTag, String> tag;
    public static volatile SingularAttribute<MediaTag, Media> media;

}