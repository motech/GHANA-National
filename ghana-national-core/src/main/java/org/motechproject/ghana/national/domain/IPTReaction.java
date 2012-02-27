package org.motechproject.ghana.national.domain;

public enum IPTReaction {
    REACTIVE("Y", Concept.REACTIVE), NONREACTIVE("N", Concept.NON_REACTIVE) {

    };
    private String key;
    private Concept reactive;

    IPTReaction(String key,  Concept reactive) {
        this.key = key;
        this.reactive = reactive;
    }

    public static IPTReaction byValue(boolean isReactive)  {
        return isReactive ? REACTIVE : NONREACTIVE;
    }

    public Concept concept() {
        return reactive;
    }
}
