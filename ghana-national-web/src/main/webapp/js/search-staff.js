$.StaffForm = function() {
    var submitForm = function() {
            $('#searchStaffForm').submit();
    };
    var bootstrap = function() {
        $("#searchStaffForm").formly({'onBlur':false, 'theme':'Light'});
        $("#searchStaff").click(submitForm);
    };
    $(bootstrap);
};

$(document).ready(function() {
    new $.StaffForm();
});