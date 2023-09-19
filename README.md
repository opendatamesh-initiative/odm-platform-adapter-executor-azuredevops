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
