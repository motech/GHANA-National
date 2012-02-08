$.UserFormValidator = function() {
    var name_reg = /^\D+$/;

    this.validate = function(form) {
        formValidator.clearMessages(form);
        formValidator.validateRequiredFields(form);
        formValidator.validatePhoneNumbers(form);
        if ($("#middleName").val().length != 0) {
            if(validateRegEx(name_reg, $("#middleName").val(), "Please enter valid middle name", $("#middleNameError")));
        }
        var regexMatch = $("#newRole option:selected").text().match(/\(.*?\)/);
        var role = (regexMatch != null) ? regexMatch[0].substring(1, regexMatch[0].length - 1) : null;
        if (role == null) {
            $("#newRoleError").html("Please select a role");
            $("#newRoleError").removeClass('hide');
        }
        if (role == 'Super Administrator' || role == 'Facility Administrator' || role == 'Call Centre Administrator' || role == 'Health Care Administrator') {
            formValidator.validateEmail(form);
        }
    };

    var validateRegEx = function (regEx, fieldValue, errorMessage, errorField) {
        if (!regEx.test(fieldValue)) {
            errorField.html(errorMessage);
            errorField.show();
            return false;
        }
        return true;
    };

};

$.StaffForm = function() {
    var validator = new $.UserFormValidator();

    var submitForm = function() {
        var staffForm = $('#staffForm');
        validator.validate(staffForm);
        if (!formValidator.hasErrors(staffForm)) {
            staffForm.submit();
        }
    };
    var bootstrap = function() {
        $("#staffForm").formly({'onBlur':false, 'theme':'Light'});
        $("#submitNewUser").click(submitForm);
    };
    $(bootstrap);
};

$(document).ready(function() {
    new $.StaffForm();
});