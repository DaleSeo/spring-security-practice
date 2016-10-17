package seo.dale.practice.spring.security.web.filter;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.FilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/technical-overview.html#tech-intro-sec-context-persistence
 * https://github.com/DaleSeo/spring-security/blob/master/web/src/test/java/org/springframework/security/web/context/SecurityContextPersistenceFilterTests.java
 */
public class SecurityContextPersistenceFilterTest {

	@Test
	public void sessionIsEagerlyCreatedWhenConfigured() throws Exception {
		final FilterChain chain = mock(FilterChain.class);
		final MockHttpServletRequest request = new MockHttpServletRequest();
		final MockHttpServletResponse response = new MockHttpServletResponse();
		SecurityContextPersistenceFilter filter = new SecurityContextPersistenceFilter();
		filter.setForceEagerSessionCreation(true);
		filter.doFilter(request, response, chain);
		assertThat(request.getSession(false)).isNotNull();
	}

	@Test
	public void nullSecurityContextRepoDoesntSaveContextOrCreateSession() throws Exception {
		final FilterChain chain = mock(FilterChain.class);
		final MockHttpServletRequest request = new MockHttpServletRequest();
		final MockHttpServletResponse response = new MockHttpServletResponse();
		SecurityContextRepository repo = new NullSecurityContextRepository();
		SecurityContextPersistenceFilter filter = new SecurityContextPersistenceFilter(repo);
		filter.doFilter(request, response, chain);
		assertThat(repo.containsContext(request)).isFalse();
		assertThat(request.getSession(false)).isNull();
	}

}
