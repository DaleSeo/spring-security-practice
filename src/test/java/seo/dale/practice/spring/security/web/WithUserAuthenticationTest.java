package seo.dale.practice.spring.security.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/test-mockmvc.html#test-mockmvc-securitycontextholder-rpp
 * https://github.com/DaleSeo/spring-security/blob/master/test/src/test/java/org/springframework/security/test/web/servlet/showcase/secured/WithUserAuthenticationTests.java
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = WithUserAuthenticationTest.Config.class)
@WebAppConfiguration
public class WithUserAuthenticationTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Test
	public void requestProtectedUrlUnauthenticated() throws Exception {
		mvc.perform(get("/"))
				.andExpect(status().isFound()) // 302
				.andExpect(unauthenticated());
	}

	@Test
	@WithMockUser
	public void requestProtectedUrlWithUser() throws Exception {
		mvc.perform(get("/"))
				.andExpect(status().isNotFound())
				.andExpect(authenticated().withUsername("user"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void requestProtectedUrlWithAdmin() throws Exception {
		mvc.perform(get("/admin"))
				.andExpect(status().isNotFound())
				.andExpect(authenticated().withUsername("user").withRoles("ADMIN"));
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

	}


}
