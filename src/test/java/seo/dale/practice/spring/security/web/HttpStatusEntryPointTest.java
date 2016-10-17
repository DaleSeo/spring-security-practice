package seo.dale.practice.spring.security.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://github.com/DaleSeo/spring-security/blob/master/web/src/test/java/org/springframework/security/web/authentication/HttpStatusEntryPointTests.java
 */
public class HttpStatusEntryPointTest {

	MockHttpServletRequest request;
	MockHttpServletResponse response;
	AuthenticationException authException;

	HttpStatusEntryPoint entryPoint;

	@Before
	public void setup() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		authException = new AuthenticationException("") {};
		entryPoint = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorNullStatus() {
		new HttpStatusEntryPoint(null);
	}

	@Test
	public void unauthorized() throws Exception {
		entryPoint.commence(request, response, authException);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
	}

}
