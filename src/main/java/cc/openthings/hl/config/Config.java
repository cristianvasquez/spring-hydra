package cc.openthings.hl.config;

import cc.openthings.hl.resources.Person;
import de.escalon.hypermedia.spring.hydra.HydraMessageConverter;
import de.escalon.hypermedia.spring.hydra.JsonLdDocumentationProvider;
import de.escalon.hypermedia.spring.xhtml.XhtmlResourceMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.RelProvider;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Sample configuration.
 * Created by dschulten on 28.12.2014.
 */
@Configuration
@EnableWebMvc
@EnablePluginRegistries(RelProvider.class)
@PropertySource("classpath:application.properties")
public class Config extends WebMvcConfigurerAdapter {

    @Autowired
    private Environment env;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(hydraMessageConverter());
        converters.add(xhtmlMessageConverter());
    }

    private HttpMessageConverter<?> xhtmlMessageConverter() {
        XhtmlResourceMessageConverter xhtmlResourceMessageConverter = new XhtmlResourceMessageConverter();
        xhtmlResourceMessageConverter.setStylesheets(
                Arrays.asList(
                        "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css"
                ));
        xhtmlResourceMessageConverter.setDocumentationProvider(new JsonLdDocumentationProvider());
        return xhtmlResourceMessageConverter;
    }

    @Bean
    public HydraMessageConverter hydraMessageConverter() {
        return new HydraMessageConverter();
    }

    @Bean
    public JsonApi2HttpConverter getJsonApiSerializer() {
        String externalURL = env.getRequiredProperty("external_url");
        String packageName = Person.class.getPackage().getName();
        JsonApi2HttpConverter jsonApiConverter = new JsonApi2HttpConverter(externalURL,packageName);
        return jsonApiConverter;
    }

}