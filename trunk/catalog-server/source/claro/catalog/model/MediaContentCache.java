package claro.catalog.model;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import claro.catalog.CatalogDao;
import claro.jpa.media.MediaContent;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public class MediaContentCache {

	private static final MediaContent empty = new MediaContent();
	
	private final CatalogDao dao;

	public MediaContentCache(CatalogDao dao) {
		this.dao = dao;
	}
	
	private final Map<Long, MediaContent> mediaContentCache = new MapMaker().expiration(7, TimeUnit.DAYS).softValues().makeComputingMap(new Function<Long, MediaContent>() {
		public MediaContent apply(Long mediaContentId) {
			MediaContent content = dao.findMediaContentById(mediaContentId);
			if (content != null) {
				content.getId();
				content.getMimeType();
				content.getData();
				content.getHash();
				return content;
			} else {
				return empty;
			}
		}
	});
	
	public MediaContent getMediaContent(Long mediaContentId) {
		MediaContent mediaContent = mediaContentCache.get(mediaContentId);
		if (mediaContent != null && mediaContent != empty) return mediaContent;
		return null;
	}
}
