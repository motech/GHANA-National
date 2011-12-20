$(document).ready(function() {

    var validate = function(form) {
        formValidator.clearMessages(form);
        formValidator.validateRequiredFields(form);

        return formValidator.hasErrors(form);
    }

    $('#searchPatient').click(function() {
        var patientForm = $('#patientForm');
        if (!validate(patientForm)) {
            patientForm.submit();
        }
    });

});