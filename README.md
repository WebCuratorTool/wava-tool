WAVA Tool
================

Web Archive Visualization and Analysis (WAVA) Tool is the standalone version of the Harvest Visualization and Analysis feature in the WebCurator Tool (WCT). It has been customized to run outside of the WCT, but still depends on core WCT code that keeps the projects in sync. WAVA Tool does not include the Patching functionality that the WCT feature has.

The WAVA Tool is intended to be used with harvests/crawls of single websites or harvests/crawls of a small set of websites. It is not designed to work with domain crawls or similar large scale harvest/crawls.


Requirements
-------------

- Apache Maven 3+ (required to build from source)

- Gradle 5.6+ (required to build from source)

- Java - WAVA Tool has been developed and testing on Java 8

- [WebCurator Tool project](https://github.com/WebCuratorTool/webcurator) - WAVA Tool requires the webcurator-core dependency from the WebCurator Tool project to be compiled and installed in the local maven repository


Configration
-------------

Configuration for the tool is set in application.properties. Located in the project here `src/main/resources/application.properties`

Common properties to configure:

- Configure the port the tool runs on. This is the port used to access the tool in your browser.

  `server.port=8080`

- Configure the directory the tool loads harvests from

  `arcDigitalAssetStoreService.baseDir=/app/harvests`

- Configure the URL for linking to a Wayback instance of a harvest

  `harvestResourceUrlMapper.urlMap=http://localhost:8090/wayback/*/`


Build
-----------

From the project directory run:

`./gradlew clean install`

The project binary is built in `build/libs/`


Harvest Setup
--------------------------

The WAVA tool can access harvests located in the directory specified in the `arcDigitalAssetStoreService.baseDir` property.

Place each harvest in its own sub-folder within the base directory.

E.g.
```
app/harvests/
|-- website_1/
  |-- website_1_file_1.warc
  |-- website_1_file_2.warc
|-- website_2/
  |-- website_2_file_1.warc
  |-- website_2_file_2.warc
```

Run
-----------

`java -jar wava-tool-<version>.war`

Open localhost:8080 in your web browser



Limitations
---------------

WAVA Tool is generally limited by the resources available. Two scenarios can cause slowness or unresponsiveness

- A harvest with a large number of URLs (5000+) within a small number of domains can cause unresponsiveness within the URL views, paricularly when filtering.

- A harvest with a large number of domains can cause the visualization to be slow to draw and interact with.



Documentation
---------------

See the [WebCurator Tool documentation](https://webcuratortool.readthedocs.io/en/latest/guides/user-manual.html#harvest-analysis) for details


