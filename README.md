# Open Data Mesh adapter for Azure DevOps

[![Build](https://github.com/opendatamesh-initiative/odm-platform-up-services-executor-azuredevops/workflows/odm-platform-up-services-executor-azuredevops%20CI/badge.svg)](https://github.com/opendatamesh-initiative/odm-platform-up-services-executor-azuredevops/actions) [![Release](https://github.com/opendatamesh-initiative/odm-platform-up-services-executor-azuredevops/workflows/odm-platform-up-services-executor-azuredevops%20CI%2FCD/badge.svg)](https://github.com/opendatamesh-initiative/odm-platform-up-services-executor-azuredevops/actions)

Open Data Mesh Platform is a platform that manages the full lifecycle of a data product from deployment to retirement. It uses the Data Product Descriptor Specification to create, deploy and operate data product containers in a mesh architecture. This repository contains the services exposed by the executor of Azure DevOps tasks.

# Azure Environment
Since the Open Data Mesh application acts as a client for Azure DevOps services, a configuration of the Azure Environment is needed. In particular, Open Data Mesh uses OAuth 2.0 as the authentication mechanism for Azure services and exploits an Azure service principal for the authorization part.

## Register Open Data Mesh as a Service Principal
Service principals are security objects within Azure AD defining what an application can do in a given Azure tenant.

1. Login into your Azure Portal, go under **Azure Active Directory** and then **App registrations**
2. Create a **New registration** with a name you desire (e.g. `odm-app`)
3. Enter your `odm-app` registration and go under **Certificates & secrets**
4. Create a new **Client secret** by choosing the name and the expiration period you want
5. Copy the client secret value in a secure place, such as a password manager (you will need it for the ODM configuration)
6. Go under **API permission**, add new permission by selecting _Azure DevOps_ from the menu, and grant `user_impersonation` permission

## Add the Service Principal to the Azure DevOps organization
Once the service principal is configured in Azure AD, you need to do the same in Azure DevOps.

1. Login into your Azure DevOps organization (`https://dev.azure.com/<your_organization_name>`) and go under **Organization settings**
2. Go under **Users** and add a new user by searching for the name of the service principal you created before
3. Grant `Basic` access level to the user

The service principal can now act as a real user on Azure DevOps in a machine-to-machine interaction.

## Useful resources
[Azure DevOps Services | Authenticate with service principals or managed identities](https://learn.microsoft.com/en-us/azure/devops/integrate/get-started/authentication/service-principal-managed-identity?view=azure-devops)

# Application configuration
To run the application and to set up the OAuth 2.0 mechanism, you need to configure the following environment variables.

### Client ID
Set an environment variable called `AZURE_ODM_APP_CLIENT_ID`. This is the Application (client) ID of the service principal.

1. Login into your Azure Portal, go under **Azure Active Directory** and then **App registrations**
2. Search for the `odm-app` app registration
3. Go to the **Overview** page and retrieve the **Application (client) ID**

### Client Secret
Set an environment variable called `AZURE_ODM_APP_CLIENT_SECRET`. This is the value of the secret you created during the Service Principal registration.

### Tenant ID
Set an environment variable called `AZURE_TENANT_ID`. This is the Tenant ID of your Azure organization.

1. Login into your Azure Portal and go under **Azure Active Directory**
2. Retrieve the **Tenant ID**

# Run it

## Prerequisites
The project requires the following dependencies:

* Java 11
* Maven 3.8.6
* Project  [odm-platform](https://github.com/opendatamesh-initiative/odm-platform)
* Register the application on Azure [Azure Environment](#azure-environment)
* Save the values created in the configuration step [Application Configuration](#application-configuration)

## Dependencies
This project need some artifacts from the odm-platform project.

### Clone dependencies repository
Clone the repository and move to the project root folder

```bash
git git clone https://github.com/opendatamesh-initiative/odm-platform.git
cd odm-platform
```

### Compile dependencies
Compile the project:

```bash
mvn clean install -DskipTests
```

## Run locally
*_Dependencies must have been compiled to run this project._

### Clone repository
Clone the repository and move to the project root folder

```bash
git git clone https://github.com/opendatamesh-initiative/odm-platform-up-services-executor-azuredevops.git
cd odm-platform-up-services-executor-azuredevops
```

### Compile project
Compile the project:

```bash
mvn clean package spring-boot:repackage -DskipTests
```

### Run application
Run the application:

```bash
java -jar azuredevops-server/target/odm-platform-up-services-executor-azuredevops-server-1.0.0.jar
```

### Stop application
To stop the application type CTRL+C or just close the shell. To start it again re-execute the following command:

```bash
java -jar azuredevops-server/target/odm-platform-up-services-executor-azuredevops-server-1.0.0.jar
```

## Run with Docker
*_Dependencies must have been compiled to run this project_

### Clone repository
Clone the repository and move it to the project root folder

```bash
git git clone https://github.com/opendatamesh-initiative/odm-platform-up-services-executor-azuredevops.git
cd odm-platform-up-services-executor-azuredevops
```

Here you can find the Dockerfile which creates an image containing the application by directly copying it from the build executed locally (i.e. from `target` folder).

### Compile project
You need to first execute the build locally by running the following command:

```bash
mvn clean package spring-boot:repackage -DskipTests
```

### Build image
Build the Docker image of the application and run it.

*Before executing the following commands change properly the value of arguments.

```bash
docker build -t odm-executor-azuredevops-app . -f Dockerfile \
   --build-arg AZURE_ODM_APP_CLIENT_ID=<azure-odm-app-client-id> \
   --build-arg AZURE_ODM_APP_CLIENT_SECRET=<azure-odm-app-client-secret> \
   --build-arg AZURE_TENANT_ID=<azure-tenant-id-value> 
```

### Run application
Run the Docker image.

```bash
docker run --name odm-executor-azuredevops-app -p 9003:9003 odm-executor-azuredevops-app
```

### Stop application

```bash
docker stop odm-executor-azuredevops-app
```
To restart a stopped application execute the following commands:

```bash
docker start odm-executor-azuredevops-app
```

To remove a stopped application to rebuild it from scratch execute the following commands :

```bash
docker rm odm-executor-azuredevops-app
```

## Run with Docker Compose
*_Dependencies must have been compiled to run this project._

### Clone repository
Clone the repository and move it to the project root folder

```bash
git git clone https://github.com/opendatamesh-initiative/odm-platform-up-services-policy-opa.git
cd odm-platform-up-services-policy-opa
```

### Compile project
You need to first execute the build locally by running the following command:

```bash
mvn clean package spring-boot:repackage -DskipTests
```

### Build image
Build the docker-compose images of the application.

Before building it, create a `.env` file in the root directory of the project similar to the following one:
```.dotenv
SPRING_PORT=9003
AZURE_ODM_APP_CLIENT_ID=<azure-odm-app-client>
AZURE_ODM_APP_CLIENT_SECRET=<azure-odm-app-client-secret>
AZURE_TENANT_ID=<azure-tenant-id-value>
```

Then, build the docker-compose file:
```bash
docker-compose build
```

### Run application
Run the docker-compose images.
```bash
docker-compose up
```

### Stop application
Stop the docker-compose images
```bash
docker-compose down
```
To restart a stopped application execute the following commands:

```bash
docker-compose up
```

To rebuild it from scratch execute the following commands :
```bash
docker-compose build --no-cache
```

# Test it

## REST services
You can invoke REST endpoints through *OpenAPI UI* available at the following url:

* [http://localhost:9003//api/v1/up/executor/azure-devops/swagger-ui/index.html](http://localhost:9003/api/v1/up/executor/azure-devops/swagger-ui/index.html)