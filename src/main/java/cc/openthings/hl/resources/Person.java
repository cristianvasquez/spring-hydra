package cc.openthings.hl.resources;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import de.escalon.hypermedia.hydra.mapping.Vocab;
import org.springframework.hateoas.ResourceSupport;

import java.util.Map;

@Vocab("http://xmlns.com/foaf/0.1/")
//@JsonApiResource(type = "Person")
public class Person extends ResourceSupport {
    public String name;

//    @Override
//    @JsonApiId
//    public Link getId() {
//        return getLink(Link.REL_SELF);
//    }

    public Person(String name) {
        this.name = name;
    }


    private Map<String, Object> other;

    @JsonAnyGetter
    public Map<String, Object> getOther() {
        return other;
    }

    @JsonAnySetter
    public void setOther(Map<String, Object> other) {
        this.other = other;
    }

}