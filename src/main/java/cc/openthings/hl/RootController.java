package cc.openthings.hl;

import cc.openthings.hl.resources.Person;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping
public class RootController {

    public static final String MEDIATYPE_JSON_API = "application/vnd.api+json";
    public static final String MEDIATYPE_JSON_LD = "application/ld+json";
    public static final String MEDIATYPE_JSON = "application/json";
    private static final String TEMPLATE = "Hello, %s!";

    @RequestMapping(path = "/jsonLd/{uuid}"
            , produces = {MEDIATYPE_JSON_LD})
    public HttpEntity<Person> jsonLd(
            @PathVariable(value = "uuid") String uuid) {
        Person resource = getPerson(uuid);
        resource.add(linkTo(methodOn(RootController.class).jsonLd(uuid)).withSelfRel());
        resource.add(linkTo(methodOn(RootController.class).jsonLd("Alice")).withRel("knows"));
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @RequestMapping(path = "/jsonApi/{uuid}", produces = {MEDIATYPE_JSON_API})
    public HttpEntity<Person> jsonApi(
            @PathVariable(value = "uuid") String uuid) {
        return jsonLd(uuid);
    }

    @RequestMapping(path = "/json/{uuid}", produces = {MEDIATYPE_JSON})
    public HttpEntity<Person> json(
            @PathVariable(value = "uuid") String uuid) {
        return jsonLd(uuid);
    }

    private Person getPerson(@PathVariable(value = "uuid") String uuid) {
        Person person = new Person(String.format(TEMPLATE, uuid));
        Map<String,Object> attributes = new LinkedHashMap<>();
        attributes.put("size", 3);
        person.setOther(attributes);
        return person;

    }
}
