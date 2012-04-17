package org.motechproject.ghana.national.domain.care;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PregnancyVaccineCareTest {

    @Test
    public void shouldCheckIfCapturedHistoryDateFallsWithinActivePregnancyPeriod(){
        final LocalDate expectedDeliveryDate = DateUtil.today().plusMonths(5);
        final Pregnancy pregnancy = Pregnancy.basedOnDeliveryDate(expectedDeliveryDate);
        final LocalDate lastTTDate = pregnancy.dateOfConception().plusMonths(2);
        IPTVaccineCare pregnancyVaccineCare=new IPTVaccineCare(mock(Patient.class), expectedDeliveryDate,false,null,lastTTDate.toDate());
        assertTrue(pregnancyVaccineCare.isWithinActiveRange());
    }

    @Test
    public void shouldCheckIfCapturedHistoryDateDoesnotFallWithinActivePregnancyPeriod(){
        final LocalDate expectedDeliveryDate = DateUtil.today().plusMonths(5);
        final Pregnancy pregnancy = Pregnancy.basedOnDeliveryDate(expectedDeliveryDate);
        final LocalDate lastTTDate = pregnancy.dateOfConception().minusMonths(2);
        IPTVaccineCare pregnancyVaccineCare=new IPTVaccineCare(mock(Patient.class), expectedDeliveryDate,false,null,lastTTDate.toDate());
        assertFalse(pregnancyVaccineCare.isWithinActiveRange());
    }

}
