## Recipe Microservice
- [Saveory Project Website]()

### Built With
IBM Cloud Microservices Starter for Java - MicroProfile / Java EE

[![](https://img.shields.io/badge/bluemix-powered-blue.svg)](https://bluemix.net)
[![Platform](https://img.shields.io/badge/platform-java-lightgrey.svg?style=flat)](https://www.ibm.com/developerworks/learn/java/)

### Table of Contents
* [Summary](#summary)
* [Requirements](#requirements)
* [Configuration](#configuration)
* [Project contents](#project-contents)
* [Run](#run)

### Summary

The Recipe Microservice manages every process related to recipe searches. Recipe searches are done by querying into the MongoDB recipes with matching ingredient names. It also interacts with the Pantry Microservice to obtain the users pantry ingredients to mark the ones that are in every recipe's ingredients list.

To deploy this application to Bluemix using a toolchain click the **Create Toolchain** button.
[![Create Toolchain](https://console.ng.bluemix.net/devops/graphics/create_toolchain_button.png)](https://console.ng.bluemix.net/devops/setup/deploy/)

### Requirements
* [Maven](https://maven.apache.org/install.html)
* Java 8: Any compliant JVM should work.
  * [Java 8 JDK from Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
  * [Java 8 JDK from IBM (AIX, Linux, z/OS, IBM i)](http://www.ibm.com/developerworks/java/jdk/),
    or [Download a Liberty server package](https://developer.ibm.com/assets/wasdev/#filter/assetTypeFilters=PRODUCT)
    that contains the IBM JDK (Windows, Linux)
* [Microservice Builder](https://developer.ibm.com/microservice-builder/#getStarted)

### Configuration
The application is configured to provide JAX-RS REST capabilities, JNDI, JSON parsing and Contexts and Dependency Injection (CDI).

These capabilities are provided through dependencies in the pom.xml file and Liberty features enabled in the server config file found in `src/main/liberty/config/server.xml`.

### Project contents
The API implementation is done in the RecipeResource.java class. Main backend functions are processed in the RecipeManager.java class. Objects that were going to be originally used with mappers are the Ingredient, Recipe Ingredient and the Recipe (due to changes in JSON management and usage we discarded their use in the Recipe Microservice)

The microservice application has a health endpoint which is accessible at `<host>:<port>/RecipeService/health`. The context root is set in the `src/main/webapp/WEB-INF/ibm-web-ext.xml` file. The ports are set in the pom.xml file and exposed to the CLI in the cli-config.yml file.

The project contains Bluemix specific files that are used to deploy the application as part of a Bluemix DevOps flow. The `.bluemix` directory contains files used to define the Bluemix toolchain and pipeline for your application. The `manifest.yml` file specifies the name of your application in Bluemix, the timeout value during deployment and which services to bind to.

This microservice application is configured to connect to the following services :

Credentials are either taken from the VCAP_SERVICES environment variable that Bluemix provides or from environment variables passed in by JNDI (see the server config file `src/main/liberty/config/server.xml`).

### Run

To build and run the application:
1. `mvn install`
1. `mvn liberty:run-server`

To build and run the application using Bluemix CLI:
1. `bx dev build`
1. `bx dev run`


To run the application in Docker use the Docker file called `Dockerfile`. If you do not want to install Maven locally you can use `Dockerfile-tools` to build a container with Maven installed.

### Endpoints

The application exposes the following endpoints:
* Health endpoint: `<host>:<port>/<contextRoot>/health`

* [API Resource Definition]()

The context root is set in the `src/main/webapp/WEB-INF/ibm-web-ext.xml` file. The ports are set in the pom.xml file and exposed to the CLI in the cli-config.yml file.

### Notices

This project was generated using:
* generator-java v1.9.1
* java-common v2.0.6
* generator-liberty v5.1.2

### License

- The LICENSE.IBM_Microservice_Builder applies to the project as a whole
- The LICENSE applies to original source code located in the `src/main/java/application/database` and `src/main/java/application/rest/v1/` folders
