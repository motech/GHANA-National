$(document).ready(function() {
    $("#searchPatientForm").formly({'onBlur':false, 'theme':'Light'});

    var validate = function(form) {
        formValidator.clearMessages(form);
        formValidator.validateRequiredFields(form);
        formValidator.validateNumber(form);

        return formValidator.hasErrors(form);
    }

    $('#searchPatient').click(function() {
        $('#search-results').hide();
        var patientForm = $('#searchPatientForm');
        if($('#motechId').val() || $('#name').val() || $('#phoneNumber').val()) {
            if (!validate(patientForm)) {
                patientForm.submit();
            }
        }else{
            $('#enter_atleast_one_field').show();
        }
    });

});