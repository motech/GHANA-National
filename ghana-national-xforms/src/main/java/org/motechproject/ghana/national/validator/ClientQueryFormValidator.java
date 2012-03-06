package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.ClientQueryForm;
import org.motechproject.ghana.national.domain.ClientQueryType;
import org.motechproject.ghana.national.domain.Constants;
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

@Component("clientQueryFormValidator")
public class ClientQueryFormValidator extends FormValidator<ClientQueryForm> {

    @Autowired
    private org.motechproject.ghana.national.validator.FormValidator formValidator;

    @LoginAsAdmin
    @ApiSession
    @Override
    public List<FormError> validate(ClientQueryForm formBean) {
        List<FormError> formErrors = super.validate(formBean);
        formErrors.addAll(formValidator.validateIfFacilityExists(formBean.getFacilityId()));
        formErrors.addAll(formValidator.validateIfStaffExists(formBean.getStaffId()));
        if (ClientQueryType.CLIENT_DETAILS.toString().equals(formBean.getQueryType())) {
            formErrors.addAll(formValidator.validatePatient(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME));
        }
        else {
            formErrors.addAll(validateIfMinimumCriteriaIsPopulated(formBean));
        }
        return formErrors;
    }

    private List<FormError> validateIfMinimumCriteriaIsPopulated(final ClientQueryForm formBean) {
        List<FormError> insufficientDataError = new ArrayList<FormError>(){{
            add(new FormError(Constants.CLIENT_QUERY_TYPE, Constants.INSUFFICIENT_SEARCH_CRITERIA));
        }};
        List<String> parameterList = nullSafeList(new ArrayList<String>() {{
            add(formBean.getFirstName());
            add(formBean.getLastName());
            add(formBean.getPhoneNumber());
            add(formBean.getNhis());
        }});
        return (parameterList.size() == 0 && formBean.getDateOfBirth() == null) ? insufficientDataError : new ArrayList<FormError>();
    }
}
