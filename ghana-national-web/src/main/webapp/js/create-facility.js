$(document).ready(function() {
    var hasSelectedValidItem = function() {
        var isValid = true;
        $('.locationAlert').each(function(index) {
            if ($(this).prev().is(":visible") && $(this).prev().find('option:selected').attr('parent') == 'select') {
                $(this).show();
                isValid = false;
                return false;
            }
        });
        return isValid;
    }

    var validate = function(facilityForm) {
        hasSelectedValidItem();
        formValidator.clearMessages(facilityForm);
        formValidator.validatePhoneNumbers(facilityForm);
        formValidator.validateRequiredFields(facilityForm);
        return formValidator.hasErrors(facilityForm);
    }

    $("#createFacilityForm").formly({'onBlur':false, 'theme':'Light'});
    new Field('countries').hasADependent(new Field('regions').hasADependent(new Field('districts').hasADependent(new Field('sub-districts'))));

    $('#submitFacility').click(function() {
        var facilityForm = $('#createFacilityForm');
        if (!validate(facilityForm)) {
            facilityForm.submit();
        }
    });

    $(".hidden-location").each(function() {
        if ($(this).children('select :selected').attr('value') == undefined) $(this).addClass('hide');
    });
});

