$(document).ready(function() {

    $('#typeOfPatient').change(function() {
        ($(this).val() == 'Child') ? $('#parentId').parent().show() : $('#parentId').parent().hide();
    });

    $("#createPatientForm").formly({'onBlur':false, 'theme':'Light'});
    new Field('regions').hasADependent(new Field('districts').hasADependent(new Field('sub-districts')));

    var validate = function(form) {
        formValidator.clearMessages(form);
        formValidator.validateRequiredFields(form);
        return formValidator.hasErrors(form);
    }

    $('#sub-districts').change(function() {
        showFacility($(this));
    });

    $('#submitNewPatient').click(function() {
        var patientForm = $('#createPatientForm');
        if (!validate(patientForm)) {
            patientForm.submit();
        }
    });
});