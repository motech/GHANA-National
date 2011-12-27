package org.motechproject.ghana.national.web.form;

import org.junit.Test;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.web.helper.PatientHelper;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPerson;

import java.text.SimpleDateFormat;
import java.util.Date;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class PatientFormTest {
    @Test
    public void shouldCreateMrsPatientObjectGivenAFormPopulatedWithData() {
        final PatientForm form = new PatientForm();
        final PatientHelper patientHelper = new PatientHelper();
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

        String country = "country";
        String facilityName = "facilityName";
        Patient patient = patientHelper.getPatientVO(form, new Facility(new MRSFacility(facilityId,facilityName, country, region, district, subDistrict)));

        MRSPerson mrsPerson = patient.mrsPatient().getPerson();

        assertThat(mrsPerson.getAddress(), is(equalTo(address)));
        assertThat(mrsPerson.getDateOfBirth(), is(equalTo(dateofBirth)));
        assertThat(patient.mrsPatient().getFacility().getId(), is(equalTo(facilityId)));
        assertThat(patient.mrsPatient().getFacility().getCountyDistrict(), is(equalTo(district)));
        assertThat(patient.mrsPatient().getFacility().getCountry(), is(equalTo(country)));
        assertThat(patient.mrsPatient().getFacility().getName(), is(equalTo(facilityName)));
        assertThat(patient.mrsPatient().getFacility().getRegion(), is(equalTo(region)));
        assertThat(patient.mrsPatient().getFacility().getStateProvince(), is(equalTo(subDistrict)));
        assertThat(mrsPerson.getBirthDateEstimated(), is(equalTo(isBirthDateEstimated)));
        assertThat(mrsPerson.getFirstName(), is(equalTo(firstName)));
        assertThat(mrsPerson.getLastName(), is(equalTo(lastName)));
        assertThat(mrsPerson.getMiddleName(), is(equalTo(middleName)));
        assertThat(patient.mrsPatient().getMotechId(), is(equalTo(motechId)));
        assertThat(mrsPerson.getPreferredName(), is(equalTo(preferredName)));
        assertThat(mrsPerson.getGender(), is(equalTo(sex)));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.NHIS_EXPIRY_DATE.getAttribute())))).value(), is(equalTo(new SimpleDateFormat(Constants.PATTERN_YYYY_MM_DD).format(nhisExpDate))));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.NHIS_NUMBER.getAttribute())))).value(), is(equalTo(nhisNumber)));
        assertThat(((Attribute) selectUnique(mrsPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.INSURED.getAttribute())))).value(), is(equalTo(insured.toString())));
    }
}
