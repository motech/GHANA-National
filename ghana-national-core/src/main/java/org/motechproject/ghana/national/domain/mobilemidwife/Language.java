package org.motechproject.ghana.national.domain.mobilemidwife;

import java.util.Arrays;
import java.util.List;

public enum Language{
    EN("English", Arrays.asList(new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.PERSONAL),
                                new MediumAndPhoneOwnership(Medium.SMS, PhoneOwnership.PERSONAL),
                                new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.HOUSEHOLD),
                                new MediumAndPhoneOwnership(Medium.SMS, PhoneOwnership.HOUSEHOLD),
                                new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.PUBLIC))),
    KAS("Kassim", Arrays.asList(new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.PERSONAL),
                                new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.HOUSEHOLD),
                                new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.PUBLIC))),
    NAN("Nankam", Arrays.asList(new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.PERSONAL),
                                new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.HOUSEHOLD),
                                new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.PUBLIC))),
    FAN("Fante", Arrays.asList(new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.PERSONAL),
                               new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.HOUSEHOLD),
                               new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.PUBLIC))),
    GD("GaAdangme", Arrays.asList(new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.HOUSEHOLD),
                                   new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.PERSONAL),
                                   new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.PUBLIC))),
    EWE("Ewe", Arrays.asList(new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.HOUSEHOLD),
                             new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.PERSONAL),
                             new MediumAndPhoneOwnership(Medium.VOICE, PhoneOwnership.PUBLIC)));

    private String displayName;
    private List<MediumAndPhoneOwnership> applicableFor;

    Language(String displayName, List<MediumAndPhoneOwnership> applicableFor) {
        this.displayName = displayName;
        this.applicableFor = applicableFor;
    }

    public String getDisplayName() {
        return displayName;
    }
    public String getValue() {
        return name();
    }

    public List<MediumAndPhoneOwnership> getApplicableFor() {
        return applicableFor;
    }

    public List<MediumAndPhoneOwnership> applicableFor(){
        return this.applicableFor;
    }

    public static class MediumAndPhoneOwnership{
        private Medium medium;
        private PhoneOwnership phoneOwnership;

        MediumAndPhoneOwnership(Medium medium, PhoneOwnership phoneOwnership) {
            this.medium = medium;
            this.phoneOwnership = phoneOwnership;
        }

        public Medium getMedium() {
            return medium;
        }

        public PhoneOwnership getPhoneOwnership() {
            return phoneOwnership;
        }
    }

}


