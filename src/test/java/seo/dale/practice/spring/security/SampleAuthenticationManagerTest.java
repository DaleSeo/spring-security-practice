package seo.dale.practice.spring.security;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 서대영(DAEYOUNG SEO)/Onestore/SKP
 */
public class SampleAuthenticationManagerTest {

	private SampleAuthenticationManager authenticationManager;

	@Before
	public void setUp() {
		authenticationManager = new SampleAuthenticationManager();
	}

	@Test
	public void testAuthenticate() {
		Authentication request = new TestingAuthenticationToken("user", "password");
		Authentication result = authenticationManager.authenticate(request);

		System.out.println(result);

		assertThat(result.getName()).isEqualTo(request.getName());
		assertThat(result.getCredentials()).isEqualTo(request.getCredentials());
		assertThat(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
	}

}