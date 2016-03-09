package cc.openthings.hl.config;

import de.escalon.hypermedia.spring.hydra.HydraMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.hateoas.RelProvider;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
@EnableWebMvc
@EnablePluginRegistries(RelProvider.class)
@PropertySource("classpath:application.properties")
public class Config extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(hydraMessageConverter());
    }

    @Bean
    public HydraMessageConverter hydraMessageConverter() {
        return new HydraMessageConverter();
    }

}