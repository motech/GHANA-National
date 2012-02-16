package org.motechproject.ghana.national.helper;

import org.junit.Test;
import org.motechproject.ghana.national.bean.PregnancyTerminationForm;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.util.DateUtil;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class MobileRequestMapperTest {
    @Test
    public void shouldCreateRequestObjectFromFormBean() {
        Date terminationDate = DateUtil.today().toDate();

        PregnancyTerminationForm form = new PregnancyTerminationForm();
        form.setComments("Comment on death");
        form.setComplications("Bleeding");
        form.setDate(terminationDate);
        form.setFacilityId("facilityId");
        form.setMaternalDeath(Boolean.TRUE);
        form.setMotechId("motechId");
        form.setProcedure("1");
        form.setReferred(Boolean.TRUE);
        form.setStaffId("staffId");
        form.setTerminationType("TerminationType");
        form.setPostAbortionFPAccepted(Boolean.TRUE);
        form.setPostAbortionFPCounseled(Boolean.TRUE);

        PregnancyTerminationRequest request = MobileRequestMapper.map(form);

        assertEquals(form.getComments(), request.getComments());
        assertReflectionEquals(form.getTerminationComplications(), request.getComplications());
        assertEquals(form.getDate(), request.getTerminationDate());
        assertEquals(form.getFacilityId(), request.getFacilityId());
        assertEquals(form.getMaternalDeath(), request.isDead());
        assertEquals(form.getMotechId(), request.getMotechId());
        assertEquals(form.getProcedure(), request.getTerminationProcedure());
        assertEquals(form.getReferred(), request.isReferred());
        assertEquals(form.getStaffId(), request.getStaffId());
        assertEquals(form.getTerminationType(), request.getTerminationType());
        assertEquals(form.getPostAbortionFPAccepted(), request.getPostAbortionFPAccepted());
        assertEquals(form.getPostAbortionFPCounseled(), request.getPostAbortionFPCounselling());
    }
}
