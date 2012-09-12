package org.motechproject.ghana.national.handler;

import org.motechproject.event.MotechEvent;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

public interface CareScheduleHandler {
    @LoginAsAdmin
    @ApiSession
    void handlePregnancyAlert(MilestoneEvent milestoneEvent);

    @LoginAsAdmin
    @ApiSession
    void handleTTVaccinationAlert(MilestoneEvent milestoneEvent);

    @LoginAsAdmin
    @ApiSession
    void handleBCGAlert(MilestoneEvent milestoneEvent);

    @LoginAsAdmin
    @ApiSession
    void handleAncVisitAlert(MotechEvent motechEvent);

    @LoginAsAdmin
    @ApiSession
    void handleIPTpVaccinationAlert(MilestoneEvent milestoneEvent);

    @LoginAsAdmin
    @ApiSession
    void handleIPTiVaccinationAlert(MilestoneEvent milestoneEvent);

    @LoginAsAdmin
    @ApiSession
    void handleMeaslesVaccinationAlert(MilestoneEvent milestoneEvent);

    @LoginAsAdmin
    @ApiSession
    void handlePentaVaccinationAlert(MilestoneEvent milestoneEvent);

    @LoginAsAdmin
    @ApiSession
    void handleYellowFeverVaccinationAlert(MilestoneEvent milestoneEvent);

    @LoginAsAdmin
    @ApiSession
    void handleOpvVaccinationAlert(MilestoneEvent milestoneEvent);

    @LoginAsAdmin
    @ApiSession
    void handlePNCMotherAlert(MilestoneEvent milestoneEvent);

    @LoginAsAdmin
    @ApiSession
    void handlePNCChildAlert(MilestoneEvent milestoneEvent);

    @LoginAsAdmin
    @ApiSession
    void handleRotavirusVaccinationAlert(MilestoneEvent milestoneEvent);

    @LoginAsAdmin
    @ApiSession
    void handlePneumococcalVaccinationAlert(MilestoneEvent milestoneEvent);
}
