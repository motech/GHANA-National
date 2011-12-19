$(document).ready(function() {

    $('#dateOfBirth').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#nhisExpirationDate').datepicker({dateFormat: "dd/mm/yy", buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:2100', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#typeOfPatient').change(function() {
        ($(this).val() == 'CHILD_UNDER_FIVE') ? $('#parentId').parent().show() : $('#parentId').parent().hide();
    });

    $('#registrationMode').change(function() {
        ($(this).val() == 'USE_PREPRINTED_ID') ? $('#motechId').parent().show() : $('#motechId').parent().hide();
    });

    $("#createPatientForm").formly({'onBlur':false, 'theme':'Light'});
    new Field('regions').hasADependent(new Field('districts').hasADependent(new Field('sub-districts')));

    var validate = function(form) {
        formValidator.clearMessages(form);
        formValidator.validateRequiredFields(form);
        formValidator.validateDate(form);
        formValidator.validateDateBefore(form);

        if(!utilities.isNull($('#nhisNumber').val()) && utilities.isNull($('#nhisExpirationDate').val())) {
            $("#nhisExpirationDateError").removeClass('hide');
        }

        if(utilities.isNull($('#nhisNumber').val()) && !utilities.isNull($('#nhisExpirationDate').val())) {
            $("#nhisNumberError").removeClass('hide');
        }

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

    $('#insured').click(function() {
        $('#nhisNumber').parent().show();
        $('#nhisExpirationDate').parent().show();
    });

    $('#notInsured').click(function() {
         $('#nhisNumber').parent().hide();
         $('#nhisExpirationDate').parent().hide();
    });







});