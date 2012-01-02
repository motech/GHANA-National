package org.motechproject.ghana.national.web.helper;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.web.StaffController;
import org.motechproject.ghana.national.web.form.StaffForm;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectUnique;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.national.domain.Constants.PERSON_ATTRIBUTE_TYPE_EMAIL;
import static org.motechproject.ghana.national.domain.Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER;
import static org.motechproject.ghana.national.domain.Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE;

@Component
public class StaffHelper {

    public String getEmail(MRSUser mrsUser) {
        MRSPerson mrsPerson = mrsUser.getPerson();
        final Attribute attribute = selectUnique(mrsPerson.getAttributes(),
                having(on(Attribute.class).name(), is(Constants.PERSON_ATTRIBUTE_TYPE_EMAIL)));
        return (attribute != null) ? attribute.value() : "";
    }

    public String getPhoneNumber(MRSUser mrsUser) {
        final Attribute attribute = selectUnique(mrsUser.getPerson().getAttributes(),
                having(on(Attribute.class).name(), is(Constants.PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER)));
        return (attribute != null) ? attribute.value() : "";
    }

    public String getRole(MRSUser mrsUser) {
        final Attribute attribute = selectUnique(mrsUser.getPerson().getAttributes(),
                having(on(Attribute.class).name(), is(Constants.PERSON_ATTRIBUTE_TYPE_STAFF_TYPE)));
        return (attribute != null) ? attribute.value() : "";
    }

    public String attrValue(List<Attribute> attributes, String key) {
        List<Attribute> filteredItems = select(attributes, having(on(Attribute.class).name(), equalTo(key)));
        return isNotEmpty(filteredItems) ? filteredItems.get(0).value() : null;
    }

    public StaffForm copyStaffValuesToForm(MRSUser mrsUser) {
        StaffForm staffForm = new StaffForm();
        MRSPerson mrsPerson = mrsUser.getPerson();
        staffForm.setId(mrsUser.getId());
        staffForm.setStaffId(mrsUser.getSystemId());
        staffForm.setFirstName(mrsPerson.getFirstName());
        staffForm.setMiddleName(mrsUser.getPerson().getMiddleName());
        staffForm.setLastName(mrsUser.getPerson().getLastName());
        List<Attribute> attributeList = mrsUser.getPerson().getAttributes();
        if (isNotEmpty(attributeList)) {
            staffForm.setNewEmail(attrValue(attributeList, PERSON_ATTRIBUTE_TYPE_EMAIL));
            staffForm.setPhoneNumber(attrValue(attributeList, PERSON_ATTRIBUTE_TYPE_PHONE_NUMBER));
            staffForm.setNewRole(attrValue(attributeList, PERSON_ATTRIBUTE_TYPE_STAFF_TYPE));
        }
        return staffForm;
    }

    public String getStaffForId(ModelMap modelMap, MRSUser mrsUser) {
        modelMap.addAttribute(StaffController.STAFF_FORM, copyStaffValuesToForm(mrsUser));
        return StaffController.EDIT_STAFF_URL;
    }

    public void populateRoles(ModelMap modelMap, Map<String, String> roles) {
        modelMap.addAttribute("roles", roles);
    }
}
