package org.opendatamesh.platform.up.executor.azuredevops.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OAuthTokenService {

    @Autowired
    private AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager;

    public String getToken() {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("azure-devops")
                .principal("odm-azure-devops-executor")
                .build();

        OAuth2AuthorizedClient authorizedClient = this.authorizedClientServiceAndManager.authorize(authorizeRequest);
        OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();

        return accessToken.getTokenValue();
    }

}

