package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.ClientQueryForm;
import org.motechproject.ghana.national.domain.ClientQueryType;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.ghana.national.domain.Constants.INSUFFICIENT_SEARCH_CRITERIA;
import static org.motechproject.ghana.national.tools.Utility.nullSafeList;

@Component
public class ClientQueryFormValidator extends FormValidator<ClientQueryForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @LoginAsAdmin
    @ApiSession
    @Override
    public List<FormError> validate(ClientQueryForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        if (ClientQueryType.CLIENT_DETAILS.equals(formBean.getClientQueryType())) {
            formErrors.addAll(formValidator.validatePatient(formBean.getMotechId(), "motechId"));
        } else {
            formErrors.add(validateIfMinimumCriteriaIsPopulated(formBean));
        }
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        return formErrors;
    }

    private FormError validateIfMinimumCriteriaIsPopulated(final ClientQueryForm formBean) {
        FormError insufficientDataError = new FormError("queryType", INSUFFICIENT_SEARCH_CRITERIA);
        List<String> parameterList = nullSafeList(new ArrayList<String>() {{
            add(formBean.getFirstName());
            add(formBean.getLastName());
            add(formBean.getPhoneNumber());
            add(formBean.getNhis());
        }});
        return (parameterList.size() == 0 && formBean.getDateOfBirth() == null) ? insufficientDataError : null;
    }
}
