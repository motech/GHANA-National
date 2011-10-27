$(document).ready(function() {
    var facilityLocationHasBeenSelected = function() {
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

    var validateForm = function() {
        utilities.clearMessages();
        var isValid = true;
        $('.phoneNo').each(function(index) {
            if(this.value != "") {
                if (!utilities.isPhoneNoValid(this.value)) {
                    $("#" + this.id + "Error").show();
                    isValid = false;
                    return false;
                }
            }
        });
        return isValid;
    }

    $("#createFacilityForm").formly({'onBlur':false, 'theme':'Light'});
    new Field('countries').hasADependent(new Field('regions').hasADependent(new Field('districts').hasADependent(new Field('sub-districts'))));

    $('#submitNewFacility').click(function() {
        if (facilityLocationHasBeenSelected() && validateForm()) {
            $('#createFacilityForm').submit();
        }
    });
});

