package org.motechproject.ghana.national.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.motechproject.ghana.national.web.form.CWCEnrollmentForm;

public class CWCEnrollmentFormMatcher extends BaseMatcher<CWCEnrollmentForm> {
    private CWCEnrollmentForm cwcEnrollmentForm;

    public CWCEnrollmentFormMatcher(CWCEnrollmentForm cwcEnrollmentForm) {
        this.cwcEnrollmentForm = cwcEnrollmentForm;
    }

    @Override
    public boolean matches(Object o) {
        boolean isEqualTo = false;
        if (o != null) {
            CWCEnrollmentForm matchedForm = (CWCEnrollmentForm) o;
            isEqualTo = matchedForm.getPatientMotechId() == null || matchedForm.getPatientMotechId().equals(cwcEnrollmentForm.getPatientMotechId());
            isEqualTo = isEqualTo && ((matchedForm.getRegistrationDate() == null) || matchedForm.getRegistrationDate().equals(cwcEnrollmentForm.getRegistrationDate()));
            isEqualTo = isEqualTo && ((matchedForm.getSerialNumber() == null) || matchedForm.getSerialNumber().equals(cwcEnrollmentForm.getSerialNumber()));
            return isEqualTo;
        }
        return isEqualTo;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("CWCEnrollmentForm is differnt");
    }

    private String randomString;
    private String another;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CWCEnrollmentFormMatcher that = (CWCEnrollmentFormMatcher) o;

        if (another != null ? !another.equals(that.another) : that.another != null) return false;
        if (randomString != null ? !randomString.equals(that.randomString) : that.randomString != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = randomString != null ? randomString.hashCode() : 0;
        result = 31 * result + (another != null ? another.hashCode() : 0);
        return result;
    }
}
