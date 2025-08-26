package HelpingYourSelf.com.HelpingYourSelf.config;



import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Value("${app.cors.allowed-origins:*}")
    private String[] allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")

                        .allowedOrigins(
                                "http://localhost:4200",      // Angular dev
                                "http://localhost:8100",      // Ionic dev
                                "http://192.168.1.64:8100",   // PC réseau local
                                "http://172.17.0.1:8100",     // Docker bridge
                                "http://172.19.0.1:8100",     // Docker autre réseau
                                "capacitor://localhost",      // Capacitor mobile
                                "ionic://localhost",          // Ionic mobile
                                "https://ton-domaine.com"     // PROD -> à remplacer
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")

                        .allowedOriginPatterns(allowedOrigins)

                        .allowedHeaders("*")
                        .allowCredentials(true);
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                String uploadPath = System.getProperty("user.dir") + "/uploads/";
                registry.addResourceHandler("/media/**")
                        .addResourceLocations("file:" + uploadPath);
            }
        };
    }
}
