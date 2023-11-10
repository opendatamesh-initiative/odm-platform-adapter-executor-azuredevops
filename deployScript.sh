#!/bin/bash

export DEBIAN_FRONTEND=noninteractive
echo 'debconf debconf/frontend select Noninteractive' | sudo debconf-set-selections

export AZURE_ODM_APP_CLIENT_ID=$1
export AZURE_ODM_APP_CLIENT_SECRET=$2
export AZURE_TENANT_ID=$3

echo AZURE_TENANT_ID: $AZURE_TENANT_ID
nohup echo AZURE_TENANT_ID_nohup: $AZURE_TENANT_ID

echo AZURE_ODM_APP_CLIENT_SECRET: $AZURE_ODM_APP_CLIENT_SECRET
nohup echo AZURE_ODM_APP_CLIENT_SECRET_nohup: $AZURE_ODM_APP_CLIENT_SECRET

sudo apt-get update ; sudo apt-get install dialog apt-utils -y; sudo apt-get install openjdk-17-jdk openjdk-17-jre -y

#mkdir -p /home/$USER/src/main/resources/db/migration && cp /home/$USER/data.csv /home/$USER/src/main/resources/db/migration/data.csv

nohup java -jar /home/$USER/odm-platform-up-services-executor-azuredevops-server-1.0.0.jar > /home/$USER/odm-platform-up-services-executor-azuredevops-server-1.0.0.jar.log 2>&1 &

echo Script lanciato