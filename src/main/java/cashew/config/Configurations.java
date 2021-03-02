package cashew.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Meter.Type;

@Configuration
@EnableTransactionManagement
@PropertySource(value = {"file:config/application.properties"})
public class Configurations implements WebMvcConfigurer {

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(Include.NON_NULL);
        return new MappingJackson2HttpMessageConverter(mapper);
    }
    
    @Bean
    MeterRegistryCustomizer<MeterRegistry> registerLoginSuccess() {
        return registry -> registry.config().namingConvention().name("services.login.success", Type.COUNTER);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> registerLoginFailure() {
        return registry -> registry.config().namingConvention().name("services.login.failure", Type.COUNTER);
    }
    
    @Bean
    MeterRegistryCustomizer<MeterRegistry> registerPaymentSuccess() {
        return registry -> registry.config().namingConvention().name("services.payment.success", Type.COUNTER);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> registerPaymentFailure() {
        return registry -> registry.config().namingConvention().name("services.payment.failure", Type.COUNTER);
    }
}
