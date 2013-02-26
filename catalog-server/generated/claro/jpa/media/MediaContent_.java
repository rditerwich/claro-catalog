package claro.jpa.media;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2013-02-22T11:24:28")
@StaticMetamodel(MediaContent.class)
public class MediaContent_ { 

    public static volatile SingularAttribute<MediaContent, Long> id;
    public static volatile SingularAttribute<MediaContent, String> hash;
    public static volatile SingularAttribute<MediaContent, byte[]> data;
    public static volatile SingularAttribute<MediaContent, String> mimeType;

}