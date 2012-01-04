$.CWCEnrollmentForm = function() {
    var submitForm = function() {
        $('#cwcEnrollmentForm').submit();
    };
    var bootstrap = function() {
        $("#cwcEnrollmentForm").formly({'onBlur':false, 'theme':'Light'});
        $("#submit").click(submitForm);
    };
    $(bootstrap);
};

$(document).ready(function() {
    new $.CWCEnrollmentForm();
    new Field('countries').hasADependent(new Field('regions').hasADependent(new Field('districts').hasADependent(new Field('sub-districts'))));
     $('#sub-districts').change(function() {
        facilities.show($(this));
    });
    $('#regions').trigger('change');
});