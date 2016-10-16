package seo.dale.practice.spring.security.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/test-mockmvc.html#test-mockmvc-securitycontextholder-rpp
 * https://github.com/DaleSeo/spring-security/blob/master/test/src/test/java/org/springframework/security/test/web/servlet/showcase/secured/SecurityRequestsTests.java
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SecurityRequestsTest.Config.class)
@WebAppConfiguration
public class SecurityRequestsTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private UserDetailsService userDetailsService;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity()) // perform all of the initial setup we need to integrate Spring Security with Spring MVC Test
				.build();
	}

	@Test
	public void requestProtectedUrlUnauthenticated() throws Exception {
		mvc.perform(get("/"))
				// Forwarded to the login page
				.andExpect(status().isFound())
				.andExpect(unauthenticated());
	}

	@Test
	public void requestProtectedUrlWithUser() throws Exception {
		mvc.perform(get("/").with(user("user")))
				// Ensure we got past Security
				.andExpect(status().isNotFound())
				// Ensure it appears we are authenticated with user
				.andExpect(authenticated().withUsername("user"));
	}

	@Test
	public void requestProtectedUrlWithAdmin() throws Exception {
		mvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
				// Ensure we got past Security
				.andExpect(status().isNotFound())
				// Ensure it appears we are authenticated with admin
				.andExpect(authenticated().withUsername("admin"));
	}

	@Test
	public void requestProtectedUrlWithUserDetails() throws Exception {
		UserDetails user = userDetailsService.loadUserByUsername("user");
		mvc.perform(get("/").with(user(user)))
				// Ensure we got past Security
				.andExpect(status().isNotFound())
				// Ensure it appears we are authenticated with user
				.andExpect(authenticated().withAuthenticationPrincipal(user));
	}

	@Test
	public void requestProtectedUrlWithAuthentication() throws Exception {
		Authentication authentication = new TestingAuthenticationToken("test", "notused", "ROLE_USER");
		mvc.perform(get("/").with(authentication(authentication)))
				// Ensure we got past Security
				.andExpect(status().isNotFound())
				// Ensure it appears we are authenticated with user
				.andExpect(authenticated().withAuthentication(authentication));
	}

	@EnableWebSecurity
	@EnableWebMvc
	static class Config extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.authorizeRequests()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
				.and()
				.formLogin();
		}

		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth
				.inMemoryAuthentication()
					.withUser("user").password("password").roles("USER");
		}

		@Override
		@Bean
		public UserDetailsService userDetailsServiceBean() throws Exception {
			return super.userDetailsServiceBean();
		}

	}


}
