package org.motechproject.ghana.national.handler;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.SMSGateway;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CareScheduleHandler extends BaseScheduleHandler {
    public static final String PREGNANCY_ALERT_SMS_KEY = "PREGNANCY_ALERT_SMS_KEY";
    public static final String TT_VACCINATION_SMS_KEY = "TT_VACCINATION_SMS_KEY";

    public CareScheduleHandler() {
    }

    @Autowired
    public CareScheduleHandler(AllPatients allPatients, AllFacilities allFacilities, SMSGateway smsGateway) {
        super(allPatients, allFacilities, smsGateway);
    }

    @LoginAsAdmin
    @ApiSession
    public void handlePregnancyAlert(final MilestoneEvent milestoneEvent) {
        LocalDate conceptionDate = milestoneEvent.getReferenceDate();
        Pregnancy pregnancy = Pregnancy.basedOnConceptionDate(conceptionDate);
        sendSMSToFacility(PREGNANCY_ALERT_SMS_KEY, milestoneEvent, pregnancy.dateOfDelivery());
    }

    @LoginAsAdmin
    @ApiSession
    public void handleTTVaccinationAlert(MilestoneEvent milestoneEvent) {
        sendSMSToFacility(TT_VACCINATION_SMS_KEY, milestoneEvent, addAggregationPeriodTo(milestoneEvent.getMilestoneAlert().getDueDate()));
    }

}
