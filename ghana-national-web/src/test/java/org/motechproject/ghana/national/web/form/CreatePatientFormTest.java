package org.motechproject.ghana.national.web.form;

import org.junit.Test;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.web.PatientController;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;

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
        final PatientController patientController = new PatientController();
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
        String phoneNumber = "0123456789";
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
        form.setPhoneNumber(phoneNumber);

        Patient patient = patientController.getPatient(form, motechId);

        assertThat(patient.mrsPatientId(), is(equalTo(null)));
        MRSPerson mrsPerson = patient.mrsPatient().getPerson();

        assertThat(mrsPerson.getAddress(), is(equalTo(address)));
        assertThat(mrsPerson.getDateOfBirth(), is(equalTo(dateofBirth)));
        assertThat(patient.mrsPatient().getFacility().getId(), is(equalTo(facilityId)));
        assertThat(patient.mrsPatient().getFacility().getCountyDistrict(), is(equalTo(null)));
        assertThat(patient.mrsPatient().getFacility().getRegion(), is(equalTo(null)));
        assertThat(patient.mrsPatient().getFacility().getStateProvince(), is(equalTo(null)));
        assertThat(mrsPerson.getBirthDateEstimated(), is(equalTo(isBirthDateEstimated)));
        assertThat(mrsPerson.getFirstName(), is(equalTo(firstName)));
        assertThat(mrsPerson.getLastName(), is(equalTo(lastName)));
        assertThat(mrsPerson.getMiddleName(), is(equalTo(middleName)));
        assertThat(patient.mrsPatient().getId(), is(equalTo(motechId)));
        assertThat(mrsPerson.getPreferredName(), is(equalTo(preferredName)));
        assertThat(mrsPerson.getGender(), is(equalTo(sex)));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.NHIS_EXPIRY_DATE.getAttribute())))).value(), is(equalTo(nhisExpDate.toString())));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.NHIS_NUMBER.getAttribute())))).value(), is(equalTo(nhisNumber)));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.INSURED.getAttribute())))).value(), is(equalTo(insured.toString())));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.PHONE_NUMBER.getAttribute())))).value(), is(equalTo(phoneNumber)));
    }
}
