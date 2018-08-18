package savings.web.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import common.json.MoneyModule;
import common.json.PercentageModule;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

@Configuration
@ComponentScan(basePackageClasses = WebConfiguration.class, excludeFilters = {
        // this filter was added to prevent interference with test configurations
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)
})
@EnableWebMvc
public class WebConfiguration extends WebMvcConfigurerAdapter {

    /*
     * This part of configuration is for classic MVC.
     */

    public void addResourceHandlers(ResourceHandlerRegistry registration) {
        registration.addResourceHandler("resources/**")
                .addResourceLocations("/resources/");
    }

    @Bean
    public ViewResolver defaultViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    // TODO #5 override one of base class methods to configure JSON message converter to automatically convert
    // JSON request body into entities and entities into JSON response body;
    public void configMessageConverter(List<HttpMessageConverter<?>> converterList) {
        converterList.add(buildJsonMessageConverter());
    }

    public static HttpMessageConverter<?> buildJsonMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(buildObjectMapper());
        return converter;
    }

    /**
     * We're using Jackson2 to handle JSON-Object mapping.
     */
    public static ObjectMapper buildObjectMapper() {
        return new ObjectMapper()
                .registerModules(
                        new PercentageModule(),
                        new MoneyModule(),
                        new JodaModule())
                .disable(
                        SerializationFeature.FAIL_ON_EMPTY_BEANS,
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
