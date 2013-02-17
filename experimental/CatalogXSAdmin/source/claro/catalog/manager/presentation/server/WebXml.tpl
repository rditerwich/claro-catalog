package claro.catalog.manager.presentation.server

import metaphor.core.IModule
import metaphor.psm.javaee.deploy.WebXml

[template AdditionalWebXmlConfiguration(IModule module) joins WebXml.AdditionalConfiguration]
    <servlet-mapping>
        <servlet-name>CatalogXSUploadMedia</servlet-name>
        <url-pattern>/CatalogManager/UploadMedia</url-pattern>
    </servlet-mapping>
    <servlet>
        <servlet-name>CatalogXSUploadMedia</servlet-name>
        <servlet-class>claro.catalog.manager.presentation.server.UploadMediaServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>CatalogXSDownloadMedia</servlet-name>
        <url-pattern>/CatalogManager/DownloadMedia</url-pattern>
    </servlet-mapping>
    <servlet>
        <servlet-name>CatalogXSDownloadMedia</servlet-name>
        <servlet-class>claro.catalog.manager.presentation.server.DownloadMediaServlet</servlet-class>
    </servlet>
[/template]