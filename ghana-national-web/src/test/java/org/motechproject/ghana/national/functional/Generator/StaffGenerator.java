package org.motechproject.ghana.national.functional.Generator;

import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.web.StaffController;
import org.motechproject.ghana.national.web.form.StaffForm;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import static org.mockito.Mockito.mock;

@Component
public class StaffGenerator {

    @Autowired
    StaffController staffController;
    AllFacilities allFacilities;

    public StaffGenerator() {
    }

    @Autowired
    public StaffGenerator(StaffController staffController, AllFacilities allFacilities) {
        this.staffController = staffController;
        this.allFacilities = allFacilities;
    }

    @Transactional(readOnly = true)
    @LoginAsAdmin
    @ApiSession
    public String createStaffAndReturnStaffId() {
        StaffForm staffForm = createStaffForm();
        BindingResult mockBindingResult = mock(BindingResult.class);
        ModelMap modelMap = new ModelMap();
        staffController.create(staffForm, mockBindingResult, modelMap);
        return (String) modelMap.get(StaffController.STAFF_ID);
    }

    private StaffForm createStaffForm() {
        return new StaffForm().setFirstName("firstName").setMiddleName("middleName").setLastName("lastName")
                .setPhoneNumber("0987654321").setNewRole("Super Admin").setNewEmail("test@test.com");

    }

}
