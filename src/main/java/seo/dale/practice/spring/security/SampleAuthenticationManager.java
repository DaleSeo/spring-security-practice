package seo.dale.practice.spring.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * authenticate if its username is "user" and its password is "password'
 */
public class SampleAuthenticationManager implements AuthenticationManager {

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		if ("user".equals(auth.getName()) && "password".equals(auth.getCredentials())) {
			return new UsernamePasswordAuthenticationToken(auth.getName(), auth.getCredentials(), AuthorityUtils.createAuthorityList("ROLE_USER"));
		}
		throw new BadCredentialsException("Bad Credentials");
	}

}
