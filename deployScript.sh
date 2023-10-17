#!/bin/bash

export AZURE_ODM_APP_CLIENT_ID=$1
export AZURE_ODM_APP_CLIENT_SECRET=$2
export AZURE_TENANT_ID=$3

sudo apt-get update ; sudo apt-get install dialog apt-utils -y; sudo apt-get install openjdk-17-jdk openjdk-17-jre -y

#mkdir -p /home/$USER/src/main/resources/db/migration && cp /home/$USER/data.csv /home/$USER/src/main/resources/db/migration/data.csv

nohup java -jar /home/$USER/odm-platform-up-services-executor-azuredevops-server-1.0.0.jar > /home/$USER/odm-platform-up-services-executor-azuredevops-server-1.0.0.jar.log 2>&1 &

echo Script lanciato