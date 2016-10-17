package seo.dale.practice.spring.security.web.filter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/technical-overview.html#exceptiontranslationfilter
 * https://github.com/DaleSeo/spring-security/blob/master/web/src/test/java/org/springframework/security/web/access/ExceptionTranslationFilterTests.java
 */
public class ExceptionTranslationFilterTest {

	@After
	@Before
	public void clearContext() throws Exception {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void successfulAccessGrant() throws Exception {
		// Setup our HTTP request
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/secure/page.html");

		// Test
		ExceptionTranslationFilter filter = new ExceptionTranslationFilter(mockEntryPoint);
		assertThat(filter.getAuthenticationEntryPoint()).isSameAs(mockEntryPoint);

		MockHttpServletResponse response = new MockHttpServletResponse();
		filter.doFilter(request, response, mock(FilterChain.class));
	}



	@Test
	public void testAccessDeniedWhenNonAnonymous() throws Exception {
		// Setup our HTTP request
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/secure/page.html");

		// Setup the FilterChain to thrown an access denied exception
		FilterChain fc = mock(FilterChain.class);
		doThrow(new AccessDeniedException("")).when(fc).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));

		// Setup SecurityContextHolder, as filter needs to check if user is anonymous
		SecurityContextHolder.clearContext();

		// Setup a new AccessDeniedHandlerImpl that will do a "forward"
		AccessDeniedHandlerImpl adh = new AccessDeniedHandlerImpl();
		adh.setErrorPage("/error.jsp");

		// Test
		ExceptionTranslationFilter filter = new ExceptionTranslationFilter(mockEntryPoint);
		filter.setAccessDeniedHandler(adh);

		MockHttpServletResponse response = new MockHttpServletResponse();
		filter.doFilter(request, response, fc);
		assertThat(response.getStatus()).isEqualTo(403);
		assertThat(request.getAttribute(WebAttributes.ACCESS_DENIED_403)).isExactlyInstanceOf(AccessDeniedException.class);
	}

	@Test
	public void testAccessDeniedWhenAnonymous() throws Exception {
		// Setup our HTTP request
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/secure/page.html");
		request.setServerPort(80);
		request.setScheme("http");
		request.setServerName("www.example.com");
		request.setContextPath("/mycontext");
		request.setRequestURI("/mycontext/secure/page.html");

		// Setup the FilterChain to thrown an access denied exception
		FilterChain fc = mock(FilterChain.class);
		doThrow(new AccessDeniedException("")).when(fc).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));

		// Setup SecurityContextHolder, as filter needs to check if user is anonymous
		SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("ignored", "ignored", AuthorityUtils.createAuthorityList("IGNORED")));

		// Test
		ExceptionTranslationFilter filter = new ExceptionTranslationFilter(mockEntryPoint);
		filter.setAuthenticationTrustResolver(new AuthenticationTrustResolverImpl());
		// assertThat(filter.getAuthenticationTrustResolver()).isNotNull();

		MockHttpServletResponse response = new MockHttpServletResponse();
		filter.doFilter(request, response, fc);
		assertThat(response.getRedirectedUrl()).isEqualTo("/mycontext/login.jsp");
		assertThat(getSavedRequestUrl(request)).isEqualTo("http://www.example.com/mycontext/secure/page.html");
	}

	private final AuthenticationEntryPoint mockEntryPoint = (request, response, authException) -> response.sendRedirect(request.getContextPath() + "/login.jsp");

	private static String getSavedRequestUrl(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session == null) {
			return null;
		}

		HttpSessionRequestCache rc = new HttpSessionRequestCache();
		SavedRequest sr = rc.getRequest(request, new MockHttpServletResponse());

		return sr.getRedirectUrl();
	}

}
