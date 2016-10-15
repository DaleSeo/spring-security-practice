package seo.dale.practice.spring.security.secured;

import org.springframework.security.access.annotation.Secured;

public class SecuredService {

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public void doSecurely() {
        System.out.println("Securely done");
    }

}
