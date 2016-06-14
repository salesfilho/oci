package br.prof.salesfilho.oci;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OciApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(OciApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setHeadless(false);
        app.run(args);
    }
}
