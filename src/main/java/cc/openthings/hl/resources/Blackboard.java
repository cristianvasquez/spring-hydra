package cc.openthings.hl.resources;

import de.escalon.hypermedia.hydra.mapping.Vocab;

@Vocab("http://cc.openthings/blackboard/")
public class Blackboard extends Concept {

    public Blackboard(String uuid) {
        super(uuid);
    }
}