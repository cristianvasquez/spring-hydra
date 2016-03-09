package cc.openthings.hl.resources;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.springframework.hateoas.ResourceSupport;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Concept extends ResourceSupport {

    private String name;
    private Map<String, Object> attributes;

    public Concept(String uuid) {
        this.name = uuid;
        this.attributes = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonAnyGetter
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @JsonAnySetter
    public void setAttributes(Map<String, Object> properties) {
        this.attributes = properties;
    }

    public <T extends Concept> T with(String name, Object value) {
        this.attributes.put(name,value);
        return (T)this;
    }

}