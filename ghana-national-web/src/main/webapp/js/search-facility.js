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
    }

    $("#facilityForm").formly({'onBlur':false, 'theme':'Light'});
    new Field('countries').hasADependent(new Field('regions').hasADependent(new Field('districts').hasADependent(new Field('sub-districts'))));

    $('#searchFacility').click(function() {
        var facilityForm = $('#facilityForm');
        if (!validate(facilityForm)) {
            facilityForm.submit();
        }
    });
});

