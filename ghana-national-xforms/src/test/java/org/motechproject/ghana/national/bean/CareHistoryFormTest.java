package org.motechproject.ghana.national.bean;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ghana.national.domain.ANCCareHistory;
import org.motechproject.ghana.national.domain.CwcCareHistory;
import org.motechproject.ghana.national.handlers.CareHistoryFormHandlerTest;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CareHistoryFormTest {

    private CareHistoryForm careHistoryForm;

    @Before
    public void setUp(){
        careHistoryForm = new CareHistoryForm();
        careHistoryForm.setAddHistory("VITA_A IPTI BCG OPV PENTA MEASLES YF IPT TT");
    }

    @Test
    public void shouldFilterUserSelectAncFormHistories(){
        assertThat(careHistoryForm.getANCCareHistories(), is(equalTo(Arrays.asList(ANCCareHistory.IPT, ANCCareHistory.TT))));
    }

    @Test
    public void shouldFilterUserSelectCwcFormHistories(){
        List<CwcCareHistory> cwcCareHistories = Arrays.asList(CwcCareHistory.VITA_A, CwcCareHistory.IPTI, CwcCareHistory.BCG, CwcCareHistory.OPV, CwcCareHistory.PENTA, CwcCareHistory.MEASLES, CwcCareHistory.YF);
        assertThat(careHistoryForm.getCWCCareHistories(), is(equalTo(cwcCareHistories)));
    }

    @Test
    public void shouldReturnACareHistoryVOInstanceGivenAFormInstance(){
        String facilityId = "facilityId";
        CareHistoryForm historyForm = CareHistoryFormHandlerTest.careHistoryFormWithCwcDetails(facilityId);
        CareHistoryFormHandlerTest.assertCareHistoryDeatils(historyForm.careHistoryVO(facilityId), historyForm, historyForm.getFacilityId());
    }
}
