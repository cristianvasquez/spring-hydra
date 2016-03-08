package cc.openthings.hl.config;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import cc.openthings.hl.RootController;
import io.katharsis.jackson.serializer.*;
import io.katharsis.locator.SampleJsonServiceLocator;
import io.katharsis.resource.field.ResourceFieldNameTransformer;
import io.katharsis.resource.information.ResourceInformationBuilder;
import io.katharsis.resource.registry.ResourceRegistry;
import io.katharsis.resource.registry.ResourceRegistryBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;
import java.util.List;

public class JsonApi2HttpConverter extends MappingJackson2HttpMessageConverter {

    public JsonApi2HttpConverter(String externalURL, String packageName) {
        this.setPrettyPrint(true);
        List<MediaType> types = Arrays.asList(
                MediaType.APPLICATION_JSON
                , MediaType.valueOf(RootController.MEDIATYPE_JSON_API)
        );
        this.setSupportedMediaTypes(types);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(getKatharsisModule(externalURL,packageName));
        this.setObjectMapper(objectMapper);
    }

    private SimpleModule getKatharsisModule(String externalURL, String packageName) {
        ResourceInformationBuilder resourceInformationBuilder =
                new ResourceInformationBuilder(new ResourceFieldNameTransformer());
        ResourceRegistryBuilder registryBuilder =
                new ResourceRegistryBuilder(new SampleJsonServiceLocator(),resourceInformationBuilder);
        ResourceRegistry resourceRegistry = registryBuilder.build(packageName, externalURL);
        SimpleModule simpleModule =
                new SimpleModule("custom jsonAPI module",new Version(1, 0, 0, null, null, null));
        simpleModule
                .addSerializer(new ContainerSerializer(resourceRegistry))
                .addSerializer(new DataLinksContainerSerializer(resourceRegistry))
                .addSerializer(new RelationshipContainerSerializer(resourceRegistry))
                .addSerializer(new LinkageContainerSerializer(resourceRegistry))
                .addSerializer(new BaseResponseSerializer(resourceRegistry))
                .addSerializer(new ErrorResponseSerializer());

        return simpleModule;
    }

}
