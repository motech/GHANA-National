package org.motechproject.ghana.national.validator;

import org.motechproject.ghana.national.bean.ClientQueryForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.openmrs.advice.ApiSession;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.ghana.national.domain.ClientQueryType.CLIENT_DETAILS;
import static org.motechproject.ghana.national.domain.ClientQueryType.FIND_CLIENT_ID;
import static org.motechproject.ghana.national.domain.ClientQueryType.UPCOMING_CARE;
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

        if (CLIENT_DETAILS.toString().equals(formBean.getQueryType()) || UPCOMING_CARE.toString().equals(formBean.getQueryType())) {
            formErrors.addAll(formValidator.validateIfPatientExistsAndIsAlive(formBean.getMotechId(), Constants.MOTECH_ID_ATTRIBUTE_NAME));
        } else if (FIND_CLIENT_ID.toString().equals(formBean.getQueryType())) {
            formErrors.addAll(validateMinimumCriteriaForFindClientID(formBean));
        }
        return formErrors;
    }

    private List<FormError> validateMinimumCriteriaForFindClientID(final ClientQueryForm formBean) {
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
