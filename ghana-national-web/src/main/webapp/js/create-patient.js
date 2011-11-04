$(document).ready(function() {

    $('#dateOfBirth').datepicker({dateFormat: "dd/mm/yy"});
    $('#nhisExpirationDate').datepicker({dateFormat: "dd/mm/yy"});
    $('#typeOfPatient').change(function() {
        ($(this).val() == 'CHILD') ? $('#parentId').parent().show() : $('#parentId').parent().hide();
    });

    $("#createPatientForm").formly({'onBlur':false, 'theme':'Light'});
    new Field('regions').hasADependent(new Field('districts').hasADependent(new Field('sub-districts')));

    var validate = function(form) {
        formValidator.clearMessages(form);
        formValidator.validateRequiredFields(form);
        return formValidator.hasErrors(form);
    }

    $('#sub-districts').change(function() {
        facilities.show($(this));
    });

    $('#submitNewPatient').click(function() {
        var patientForm = $('#createPatientForm');
        if (!validate(patientForm)) {
            patientForm.submit();
        }
    });
});