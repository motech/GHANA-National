package org.motechproject.ghana.national.validator.patient;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.util.DateUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.ghana.national.domain.Constants.CHILD_AGE_MORE_ERR_MSG;
import static org.motechproject.ghana.national.domain.Constants.CHILD_AGE_PARAMETER;

public class IsFormSubmittedForAChildTest {

    


    @Test
    public void shouldVerifyChildIsLessThanFiveYearsOfAge() {

        //child is less than five years of age
        Date dateOfBirth = DateUtil.now().minusYears(4).toDate();
        IsFormSubmittedForAChild validator = new IsFormSubmittedForAChild(dateOfBirth);
        List<FormError> errors = validator.validate(new Patient(), Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList());
        assertThat(errors.size(),is(0));

        //child is above five years
        dateOfBirth = DateUtil.now().minusYears(6).toDate();
        validator = new IsFormSubmittedForAChild(dateOfBirth);
        errors = validator.validate(new Patient(), Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList());
        assertThat(errors.size(),is(1));
        assertThat(errors, Matchers.hasItem(new FormError(CHILD_AGE_PARAMETER, CHILD_AGE_MORE_ERR_MSG)));
    }
}
