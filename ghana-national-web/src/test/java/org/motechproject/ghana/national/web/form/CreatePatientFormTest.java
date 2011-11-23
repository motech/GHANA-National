package org.motechproject.ghana.national.web.form;

import org.junit.Test;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.mrs.model.Attribute;

import java.util.Date;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectUnique;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CreatePatientFormTest {
    @Test
    public void shouldCreateMrsPatientObjectGivenAFormPopulatedWithData() {
        final CreatePatientForm form = new CreatePatientForm();
        String address = "Address";
        Date dateofBirth = new Date(10, 10, 2011);
        String district = "District";
        Boolean isBirthDateEstimated = true;
        String facilityId = "FacilityID";
        String firstName = "FirstName";
        Boolean insured = true;
        String lastName = "LastName";
        String middleName = "MiddleName";
        String motechId = "motechId";
        Date nhisExpDate = new Date(10, 10, 2022);
        String nhisNumber = "NhisNumber";
        String parentId = "ParentId";
        String preferredName = "PreferredName";
        String region = "Region";
        RegistrationType registrationMode = RegistrationType.USE_PREPRINTED_ID;
        String sex = "M";
        String subDistrict = "SubDistrict";
        PatientType patientType = PatientType.CHILD_UNDER_FIVE;

        form.setAddress(address);
        form.setDateOfBirth(dateofBirth);
        form.setDistrict(district);
        form.setEstimatedDateOfBirth(isBirthDateEstimated);
        form.setFacilityId(facilityId);
        form.setFirstName(firstName);
        form.setInsured(insured);
        form.setLastName(lastName);
        form.setMiddleName(middleName);
        form.setMotechId(motechId);
        form.setNhisExpirationDate(nhisExpDate);
        form.setNhisNumber(nhisNumber);
        form.setParentId(parentId);
        form.setPreferredName(preferredName);
        form.setRegion(region);
        form.setRegistrationMode(registrationMode);
        form.setSex(sex);
        form.setSubDistrict(subDistrict);
        form.setTypeOfPatient(patientType);

        Patient patient = form.getPatient(motechId);

        assertThat(patient.mrsPatientId(), is(equalTo(null)));

        assertThat(patient.mrsPatient().getAddress(), is(equalTo(address)));
        assertThat(patient.mrsPatient().getDateOfBirth(), is(equalTo(dateofBirth)));
        assertThat(patient.mrsPatient().getFacility().getId(), is(equalTo(facilityId)));
        assertThat(patient.mrsPatient().getFacility().getCountyDistrict(), is(equalTo(null)));
        assertThat(patient.mrsPatient().getFacility().getRegion(), is(equalTo(null)));
        assertThat(patient.mrsPatient().getFacility().getStateProvince(), is(equalTo(null)));
        assertThat(patient.mrsPatient().getBirthDateEstimated(), is(equalTo(isBirthDateEstimated)));
        assertThat(patient.mrsPatient().getFirstName(), is(equalTo(firstName)));
        assertThat(patient.mrsPatient().getLastName(), is(equalTo(lastName)));
        assertThat(patient.mrsPatient().getMiddleName(), is(equalTo(middleName)));
        assertThat(patient.mrsPatient().getId(), is(equalTo(motechId)));
        assertThat(patient.mrsPatient().getPreferredName(), is(equalTo(preferredName)));
        assertThat(patient.mrsPatient().getGender(), is(equalTo(sex)));
        assertThat(((Attribute) selectUnique(patient.mrsPatient().getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.NHIS_EXPIRY_DATE.getAttribute())))).value(), is(equalTo(nhisExpDate.toString())));
        assertThat(((Attribute) selectUnique(patient.mrsPatient().getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.NHIS_NUMBER.getAttribute())))).value(), is(equalTo(nhisNumber)));
        assertThat(((Attribute) selectUnique(patient.mrsPatient().getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.INSURED.getAttribute())))).value(), is(equalTo(insured.toString())));
    }
}
