package claro.catalog.manager.client.importing;

import java.util.ArrayList;
import java.util.List;

import claro.catalog.command.importing.DeleteImportedData;
import claro.catalog.command.importing.DeleteImportedData.Result;
import claro.catalog.command.importing.GetImportSources;
import claro.catalog.command.importing.StoreImportSource;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.command.StatusCallback;
import claro.jpa.importing.ImportJobResult;
import claro.jpa.importing.ImportProducts;
import claro.jpa.importing.ImportRules;
import claro.jpa.importing.ImportSource;
import claro.jpa.importing.TabularFileFormat;
import claro.jpa.importing.XmlFileFormat;
import claro.jpa.jobs.Job;
import claro.jpa.jobs.JobResult;

import com.google.common.base.Objects;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.ObjectUtil;

public abstract class ImportSoureModel implements Globals {

	private List<ImportSource> importSources = new ArrayList<ImportSource>();
	private ImportSource importSource;
	private ImportRules rules;
	private String currentNestedFile;
	private ImportJobResult jobResult;

	public List<ImportSource> getImportSources() {
		return importSources;
	}
	
	public void setImportSources(List<ImportSource> importSources) {
		this.importSources = importSources;
		renderAll();
	}
	
	public void setImportSource(ImportSource importSource) {
		this.importSource = importSource;
		rules = null;
		jobResult = null;
		renderAll();
	}
	
	public ImportSource getImportSource() {
		return importSource;
	}
	
	public ImportRules getRules() {
		if (importSource == null) return null;
		if (rules == null) {
			// find rules based on currentNestedFile
			for (ImportRules r : importSource.getRules()) {
				if (Objects.equal(r.getRelativeUrl(), currentNestedFile)) {
					rules = r;
				}
			}
			// not found? get first one
			if (rules == null && !importSource.getRules().isEmpty()) {
				rules = importSource.getRules().get(0);
				currentNestedFile = rules.getRelativeUrl();
			}
			
			// create new rules?
			if (rules == null) {
				currentNestedFile = "";
				rules = new ImportRules();
				rules.setImportSource(importSource);
				importSource.getRules().add(rules);
			}
		}

		// take the smallest relative url if not multi-file
		if (!importSource.getMultiFileImport() && importSource.getRules().size() > 1) {
			for (ImportRules r : importSource.getRules()) {
				if (r.getRelativeUrl().compareTo(rules.getRelativeUrl()) < 0) {
					rules = r;
				}
			}
		}
		
		return rules;
	}
	
	public void setRules(int selectedIndex) {
		if (selectedIndex >= 0 && selectedIndex < importSource.getRules().size()) {
			ImportRules r = importSource.getRules().get(selectedIndex);
			if (rules != r) {
				rules = r;
				currentNestedFile = rules.getRelativeUrl();
			}
			renderAll();
		}
	}
	
	public void setRules(ImportRules rules) {
		setRules(importSource.getRules().indexOf(rules));
	}

	public ImportProducts getImportProducts() {
		ImportRules rules = getRules();
		if (rules != null) {
			ImportProducts p = rules.getImportProducts();
			if (p == null) {
				p = new ImportProducts();
				rules.setImportProducts(p);
				p.setRules(rules);
			}
			return p;
		}
		return null;
	}
	
	public TabularFileFormat getCSVFileFormat() {
		ImportRules rules = getRules();
		if (rules != null) {
			TabularFileFormat fileFormat = rules.getTabularFileFormat();
			if (fileFormat == null) {
				fileFormat = new TabularFileFormat();
				rules.setTabularFileFormat(fileFormat);
			}
			return fileFormat;
		}
		return null;
	}
	
	public XmlFileFormat getXMLFileFormat() {
		ImportRules rules = getRules();
		if (rules != null) {
			XmlFileFormat fileFormat = rules.getXmlFileFormat();
			if (fileFormat == null) {
				fileFormat = new XmlFileFormat();
				rules.setXmlFileFormat(fileFormat);
			}
			return fileFormat;
		}
		return null;
	}
	
	public void setJobResult(ImportJobResult jobResult) {
		if (this.jobResult != jobResult) {
			this.jobResult = jobResult;
			renderAll();
		}
	}

	public ImportJobResult getJobResult() {
		if (jobResult == null) {
			for (JobResult r : importSource.getJob().getResults()) {
				if (jobResult == null || ObjectUtil.compare(jobResult.getEndTime(), r.getEndTime()) < 0) {
					jobResult = (ImportJobResult) r;
				}
			}
		}
		return jobResult;
	}
	
	public void store(final StoreImportSource command) {
		GwtCommandFacade.execute(command, new StatusCallback<StoreImportSource.Result>() {
			public void onSuccess(StoreImportSource.Result result) {
				if (result.importSource != null) {
						int index = CollectionUtil.indexOfRef(importSources, command.importSource);
						if (index != -1) {
							importSources.set(index, result.importSource);
						}
				} else {
					importSources.remove(command.importSource);
				}
				setImportSource(result.importSource);
			}
		});
	}

//	protected void createImportSource() {
//		StoreImportSource command = new StoreImportSource(new ImportSource());
//		GwtCommandFacade.executeWithRetry(command, 3, new StatusCallback<StoreImportSource.Result>(messages.creatingImportSourceMessage()) {
//			public void onSuccess(StoreImportSource.Result result) {
//			}
//		});
//	}
	

	final protected void createImportSource() {
		ImportSource importSource = new ImportSource();
		importSource.setName("new import source");
		importSource.setJob(new Job());
		getImportSources().add(0, importSource);
		setImportSource(importSource);
		openDetail();
	}

	public void fetchImportSources() {
		GetImportSources command = new GetImportSources();
		GwtCommandFacade.executeWithRetry(command, 3, new StatusCallback<GetImportSources.Result>(messages.loadingImportSourcesMessage()) {
			public void onSuccess(GetImportSources.Result result) {
				setImportSources(result.importSources);
			}
		});
	}
//		
//	protected void getImportSource(final ImportSource importSource) {
//		GetImportSources command = new GetImportSources();
//		command.importSourceId = importSource.getId();
//		command.includeDefinitionDetails = true;
//		GwtCommandFacade.executeWithRetry(command, 3, new StatusCallback<GetImportSources.Result>() {
//			public void onSuccess(GetImportSources.Result result) {
//				setImportSource(importSource);
////				masterDetail.importSourceChanged(importSource, firstOrNull(result.importSources));
//			}
//		});
//	}

	protected abstract void renderAll();
	protected abstract void showFileFormat();
	protected abstract void showDataMapping();
	protected abstract void openDetail();
	protected abstract void showLog();

	public void deleteImportedData() {
		GwtCommandFacade.execute(new DeleteImportedData(importSource), new StatusCallback<DeleteImportedData.Result>() {
			public void onSuccess(Result result) {
			}
		});
	}

}
