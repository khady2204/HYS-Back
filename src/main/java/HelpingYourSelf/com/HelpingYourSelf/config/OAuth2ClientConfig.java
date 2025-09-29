package HelpingYourSelf.com.HelpingYourSelf.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(OAuth2ClientProperties.class)
public class OAuth2ClientConfig {

    private static final Logger log = LoggerFactory.getLogger(OAuth2ClientConfig.class);

    private final OAuth2ClientProperties properties;

    public OAuth2ClientConfig(OAuth2ClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = properties.getRegistration().entrySet().stream()
                .map(this::mapRegistration)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (registrations.isEmpty()) {
            log.warn("No OAuth2 client registrations configured - OAuth2 client features will be disabled");
            return registrationId -> null;
        }

        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    private ClientRegistration mapRegistration(Map.Entry<String, OAuth2ClientProperties.Registration> entry) {
        String registrationId = entry.getKey();
        OAuth2ClientProperties.Registration registration = entry.getValue();

        if (registration.getClientId() == null || registration.getClientSecret() == null) {
            return null;
        }

        AuthorizationGrantType grantType = registration.getAuthorizationGrantType() != null
                ? new AuthorizationGrantType(registration.getAuthorizationGrantType())
                : AuthorizationGrantType.AUTHORIZATION_CODE;

        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId)
                .clientId(registration.getClientId())
                .clientSecret(registration.getClientSecret())
                .clientName(registration.getClientName() != null ? registration.getClientName() : registrationId)
                .authorizationGrantType(grantType)
                .redirectUri(registration.getRedirectUri())
                .scope(registration.getScope());

        OAuth2ClientProperties.Provider provider = resolveProvider(registration, registrationId);

        if (provider != null) {
            builder.authorizationUri(provider.getAuthorizationUri());
            builder.tokenUri(provider.getTokenUri());
            builder.userInfoUri(provider.getUserInfoUri());
            builder.userNameAttributeName(provider.getUserNameAttribute());
            if (provider.getJwkSetUri() != null) {
                builder.jwkSetUri(provider.getJwkSetUri());
            }
        }

        return builder.build();
    }

    private OAuth2ClientProperties.Provider resolveProvider(OAuth2ClientProperties.Registration registration, String registrationId) {
        if (registration.getProvider() != null) {
            OAuth2ClientProperties.Provider provider = properties.getProvider().get(registration.getProvider());
            if (provider != null) {
                return provider;
            }
        }
        return properties.getProvider().get(registrationId);
    }
}
