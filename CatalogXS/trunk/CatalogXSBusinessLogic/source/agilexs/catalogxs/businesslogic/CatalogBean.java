package agilexs.catalogxs.businesslogic;

import javax.ejb.Stateless;

@Stateless
public class CatalogBean extends CatalogBeanBase implements Catalog {

//	private final static org.apache.log4j.Logger LOGGER = eu.future.earth.logging.ExtendedLog.getLogger(CatalogBean.class);

//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
//	public Product updateProduct(Product oldProduct, Product newProduct) {
//		Product result = super.updateProduct(oldProduct, newProduct);
//		LuceneHelper helper = new LuceneHelper();
//		try {
//			IndexWriter writer = helper.getWriter();
//			ProductAnalyzer analyzer = new ProductAnalyzer();
//			Document doc = analyzer.createDocument(result);
//			if (oldProduct != null) {
//				writer.updateDocument(analyzer.createKey(result), doc);
//			} else {
//				writer.addDocument(doc);
//			}
//			writer.optimize();
//			writer.close();
//		} catch (CorruptIndexException e) {
//			LOGGER.error("Corrupted Index.", e);
//		} catch (LockObtainFailedException e) {
//			LOGGER.error("Could not get lock.", e);
//		} catch (IOException e) {
//			LOGGER.error("File Error.", e);
//		}
//		return result;
//	}
}