package seo.dale.practice.spring.security.prepost;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import seo.dale.practice.spring.security.prepost.service.HelloMessageService;
import seo.dale.practice.spring.security.prepost.service.MessageService;

/**
 * http://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/test-method.html#test-method-withmockuser
 * https://github.com/spring-projects/spring-security/blob/master/test/src/test/java/org/springframework/security/test/context/showcase/WithMockUserTests.java
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = WithMockUserTest.Config.class)
public class WithMockUserTest {

	@Autowired
	private MessageService service;

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void getMessageUnauthenticated() {
		service.getMessage();
	}

	@EnableGlobalMethodSecurity(prePostEnabled = true)
	@ComponentScan(basePackageClasses = HelloMessageService.class)
	static class Config {

		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth
				.inMemoryAuthentication()
					.withUser("user").password("password").roles("USER");
		}

	}

}