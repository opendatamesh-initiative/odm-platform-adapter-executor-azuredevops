package org.opendatamesh.platform.up.executor.azuredevops.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"org.opendatamesh.platform.up.executor.azuredevops"})
public class AzureDevOpsExecutorApplication {
    public static void main(String[] args) {
        SpringApplication.run(AzureDevOpsExecutorApplication.class, args);
    }
}