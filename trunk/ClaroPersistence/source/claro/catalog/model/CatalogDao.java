package claro.catalog.model;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import claro.jpa.catalog.Item;
import claro.jpa.catalog.ParentChild;

public class CatalogDao {

	private final EntityManager em;

	public CatalogDao(EntityManager em) {
		this.em = em;
  }
	
	public void setItemParents(Item item, List<Item> parents) {
		for (ParentChild currentParentChild : item.getParents()) {
			if (currentParentChild.getParent() != null) {
				
			}
		}
		Collection<ParentChild> parentChildList = item.getParents();
	}
}
