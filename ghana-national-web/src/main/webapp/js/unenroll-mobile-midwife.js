$(document).ready(function() {
    new Field('countries').hasADependent(new Field('regions').hasADependent(new Field('districts').hasADependent(new Field('sub-districts'))));
    $('#sub-districts').change(function() {
        facilities.show($(this));
    });
    $('#regions').trigger('change');
    $("#mobileMidwifeUnEnrollForm").formly({'onBlur':false, 'theme':'Light'});

    var validate = function(form) {
        formValidator.clearMessages(form);
        formValidator.validateRequiredFields(form);
        return formValidator.hasErrors(form);
    };

    $('#unregisterMobileMidwife').click(function() {
        var form = $('#mobileMidwifeUnEnrollForm');
        if (!validate(form)) {
            form.submit();
        }
    });

});
