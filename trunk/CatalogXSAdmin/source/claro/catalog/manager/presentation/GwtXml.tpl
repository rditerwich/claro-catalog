package claro.catalog.manager.presentation

import metaphor.psm.gwt.IGwtModule
import metaphor.psm.gwt.deploy.GwtModuleFile

[template CatalogXSAdditionalModuleInheritance(IGwtModule module) constraint module.name.equals("CatalogManager") joins GwtModuleFile.AdditionalModuleInheritance]
  <!-- any one of the following lines.                        -->
  <inherits name='claro.catalog.presentation.theme.catalogmanager.CatalogManager' />
[/template]
