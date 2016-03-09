package cc.openthings.hl;

import cc.openthings.hl.resources.Agent;
import cc.openthings.hl.resources.Blackboard;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping
public class RootController {

    public static final String MEDIATYPE_JSON_LD = "application/ld+json";
    public static final String MEDIATYPE_JSON = "application/json";

    @RequestMapping(path = "/testHal/{uuid}", produces = {MEDIATYPE_JSON})
    public HttpEntity<ResourceSupport> testHal(
            @PathVariable(value = "uuid") String uuid) {
        return agent(uuid);
    }

    @RequestMapping(produces = {MEDIATYPE_JSON})
    public HttpEntity<ResourceSupport> index() {
        ResourceSupport resource = new ResourceSupport();
        resource.add(linkTo(methodOn(RootController.class).blackboard("Music")).withRel("cc:contains"));
        resource.add(linkTo(methodOn(RootController.class).agent("Cristian")).withRel("cc:contains"));
        resource.add(linkTo(methodOn(RootController.class).testHal("Testing Hal")).withRel("cc:contains"));
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * Blackboard
     */
    @RequestMapping(path = "/blackboard/{uuid}", produces = {MEDIATYPE_JSON_LD, MEDIATYPE_JSON})
    public HttpEntity<ResourceSupport> blackboard(@PathVariable(value = "uuid") String uuid) {
        Blackboard resource = getBlackboard(uuid);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    private Blackboard getBlackboard(String uuid) {
        Blackboard resource = new Blackboard(String.format("Blackboard uuid:%s", uuid))
                .with("size",3)
                .with("substrate",223);
        resource.add(linkTo(methodOn(RootController.class).blackboard(uuid)).withSelfRel());

        return resource;
    }

    /**
     * Agent
     */
    @RequestMapping(path = "/agent/{uuid}", produces = {MEDIATYPE_JSON_LD, MEDIATYPE_JSON})
    public HttpEntity<ResourceSupport> agent(@PathVariable(value = "uuid") String uuid) {
        return new ResponseEntity<>(getAgent(uuid), HttpStatus.OK);
    }

    private Agent getAgent(String uuid) {
        Agent resource = new Agent(String.format("Agent uuid:%s", uuid))
                .with("age",22);
        resource.add(linkTo(methodOn(RootController.class).agent(uuid)).withSelfRel());

        // Commits to some blackboards
        resource.add(linkTo(methodOn(RootController.class).blackboard("Music")).withRel("cc:commitsTo"));

        // Knows some people
        resource.add(linkTo(methodOn(RootController.class).agent("Alice")).withRel("foaf:knows"));
        resource.add(linkTo(methodOn(RootController.class).agent("Bob")).withRel("foaf:knows"));

        return resource;
    }

}
