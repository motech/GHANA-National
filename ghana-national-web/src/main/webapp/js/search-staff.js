$.StaffForm = function() {
    var submitForm = function() {
            $('#staffForm').submit();
    };
    var bootstrap = function() {
        $("#staffForm").formly({'onBlur':false, 'theme':'Light'});
        $("#searchStaff").click(submitForm);
    };
    $(bootstrap);
};

$(document).ready(function() {
    new $.StaffForm();
});