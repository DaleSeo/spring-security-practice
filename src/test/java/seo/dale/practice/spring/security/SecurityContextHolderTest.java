package seo.dale.practice.spring.security;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SecurityContextHolderTest {

	@Before
	public final void setUp() {
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	}

	@Test
	public void testContextHolderGetterSetterClearer() {
		SecurityContext sc =  new SecurityContextImpl();
		sc.setAuthentication(new UsernamePasswordAuthenticationToken("Foobar", "pass"));
		SecurityContextHolder.setContext(sc);
		assertThat(SecurityContextHolder.getContext()).isEqualTo(sc);
		SecurityContextHolder.clearContext();
		assertThat(SecurityContextHolder.getContext()).isNotSameAs(sc);
		SecurityContextHolder.clearContext();
	}

	@Test
	public void testNeverReturnsNull() {
		assertThat(SecurityContextHolder.getContext()).isNotNull();
		SecurityContextHolder.clearContext();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRejectsNulls() {
		SecurityContextHolder.setContext(null);
		fail("Should have rejected null");
	}

}