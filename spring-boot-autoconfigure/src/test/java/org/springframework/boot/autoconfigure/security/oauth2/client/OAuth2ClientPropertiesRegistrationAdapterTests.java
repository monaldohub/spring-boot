/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.security.oauth2.client;

import java.util.Collections;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Provider;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties.Registration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ProviderDetails;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OAuth2ClientPropertiesRegistrationAdapter}.
 *
 * @author Phillip Webb
 */
public class OAuth2ClientPropertiesRegistrationAdapterTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void getClientRegistrationsWhenUsingDefinedProviderShouldAdapt()
			throws Exception {
		OAuth2ClientProperties properties = new OAuth2ClientProperties();
		Provider provider = new Provider();
		provider.setAuthorizationUri("http://example.com/auth");
		provider.setTokenUri("http://example.com/token");
		provider.setUserInfoUri("http://example.com/info");
		provider.setJwkSetUri("http://example.com/jkw");
		Registration registration = new Registration();
		registration.setProvider("provider");
		registration.setClientId("clientId");
		registration.setClientSecret("clientSecret");
		registration.setClientAuthenticationMethod(ClientAuthenticationMethod.POST);
		registration.setAuthorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
		registration.setRedirectUri("http://example.com/redirect");
		registration.setScope(Collections.singleton("scope"));
		registration.setClientName("clientName");
		properties.getProvider().put("provider", provider);
		properties.getRegistration().put("registration", registration);
		Map<String, ClientRegistration> registrations = OAuth2ClientPropertiesRegistrationAdapter
				.getClientRegistrations(properties);
		ClientRegistration adapted = registrations.get("registration");
		ProviderDetails adaptedProvider = adapted.getProviderDetails();
		assertThat(adaptedProvider.getAuthorizationUri())
				.isEqualTo("http://example.com/auth");
		assertThat(adaptedProvider.getTokenUri()).isEqualTo("http://example.com/token");
		assertThat(adaptedProvider.getUserInfoEndpoint().getUri()).isEqualTo("http://example.com/info");
		assertThat(adaptedProvider.getJwkSetUri()).isEqualTo("http://example.com/jkw");
		assertThat(adapted.getRegistrationId()).isEqualTo("registration");
		assertThat(adapted.getClientId()).isEqualTo("clientId");
		assertThat(adapted.getClientSecret()).isEqualTo("clientSecret");
		assertThat(adapted.getClientAuthenticationMethod()).isEqualTo(
				org.springframework.security.oauth2.core.ClientAuthenticationMethod.POST);
		assertThat(adapted.getAuthorizationGrantType()).isEqualTo(
				org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE);
		assertThat(adapted.getRedirectUri()).isEqualTo("http://example.com/redirect");
		assertThat(adapted.getScope()).containsExactly("scope");
		assertThat(adapted.getClientName()).isEqualTo("clientName");
	}

	@Test
	public void getClientRegistrationsWhenUsingCommonProviderShouldAdapt()
			throws Exception {
		OAuth2ClientProperties properties = new OAuth2ClientProperties();
		Registration registration = new Registration();
		registration.setProvider("google");
		registration.setClientId("clientId");
		registration.setClientSecret("clientSecret");
		properties.getRegistration().put("registration", registration);
		Map<String, ClientRegistration> registrations = OAuth2ClientPropertiesRegistrationAdapter
				.getClientRegistrations(properties);
		ClientRegistration adapted = registrations.get("registration");
		ProviderDetails adaptedProvider = adapted.getProviderDetails();
		assertThat(adaptedProvider.getAuthorizationUri())
				.isEqualTo("https://accounts.google.com/o/oauth2/v2/auth");
		assertThat(adaptedProvider.getTokenUri())
				.isEqualTo("https://www.googleapis.com/oauth2/v4/token");
		assertThat(adaptedProvider.getUserInfoEndpoint().getUri())
				.isEqualTo("https://www.googleapis.com/oauth2/v3/userinfo");
		assertThat(adaptedProvider.getJwkSetUri())
				.isEqualTo("https://www.googleapis.com/oauth2/v3/certs");
		assertThat(adapted.getRegistrationId()).isEqualTo("registration");
		assertThat(adapted.getClientId()).isEqualTo("clientId");
		assertThat(adapted.getClientSecret()).isEqualTo("clientSecret");
		assertThat(adapted.getClientAuthenticationMethod()).isEqualTo(
				org.springframework.security.oauth2.core.ClientAuthenticationMethod.BASIC);
		assertThat(adapted.getAuthorizationGrantType()).isEqualTo(
				org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE);
		assertThat(adapted.getRedirectUri()).isEqualTo(
				"{scheme}://{serverName}:{serverPort}{contextPath}/oauth2/authorize/code/{clientAlias}");
		assertThat(adapted.getScope()).containsExactly("openid", "profile", "email",
				"address", "phone");
		assertThat(adapted.getClientName()).isEqualTo("Google");
	}

	@Test
	public void getClientRegistrationsWhenUsingCommonProviderWithOverrideShouldAdapt()
			throws Exception {
		OAuth2ClientProperties properties = new OAuth2ClientProperties();
		Registration registration = new Registration();
		registration.setProvider("google");
		registration.setClientId("clientId");
		registration.setClientSecret("clientSecret");
		registration.setClientAuthenticationMethod(ClientAuthenticationMethod.POST);
		registration.setAuthorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
		registration.setRedirectUri("http://example.com/redirect");
		registration.setScope(Collections.singleton("scope"));
		registration.setClientName("clientName");
		properties.getRegistration().put("registration", registration);
		Map<String, ClientRegistration> registrations = OAuth2ClientPropertiesRegistrationAdapter
				.getClientRegistrations(properties);
		ClientRegistration adapted = registrations.get("registration");
		ProviderDetails adaptedProvider = adapted.getProviderDetails();
		assertThat(adaptedProvider.getAuthorizationUri())
				.isEqualTo("https://accounts.google.com/o/oauth2/v2/auth");
		assertThat(adaptedProvider.getTokenUri())
				.isEqualTo("https://www.googleapis.com/oauth2/v4/token");
		assertThat(adaptedProvider.getUserInfoEndpoint().getUri())
				.isEqualTo("https://www.googleapis.com/oauth2/v3/userinfo");
		assertThat(adaptedProvider.getJwkSetUri())
				.isEqualTo("https://www.googleapis.com/oauth2/v3/certs");
		assertThat(adapted.getRegistrationId()).isEqualTo("registration");
		assertThat(adapted.getClientId()).isEqualTo("clientId");
		assertThat(adapted.getClientSecret()).isEqualTo("clientSecret");
		assertThat(adapted.getClientAuthenticationMethod()).isEqualTo(
				org.springframework.security.oauth2.core.ClientAuthenticationMethod.POST);
		assertThat(adapted.getAuthorizationGrantType()).isEqualTo(
				org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE);
		assertThat(adapted.getRedirectUri()).isEqualTo("http://example.com/redirect");
		assertThat(adapted.getScope()).containsExactly("scope");
		assertThat(adapted.getClientName()).isEqualTo("clientName");
	}

	@Test
	public void getClientRegistrationsWhenUnknownProviderShouldThrowException()
			throws Exception {
		OAuth2ClientProperties properties = new OAuth2ClientProperties();
		Registration registration = new Registration();
		registration.setProvider("missing");
		properties.getRegistration().put("registration", registration);
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unknown provider ID 'missing'");
		OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties);
	}

}
