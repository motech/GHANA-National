$.CreateUser = function() {
    var submitForm = function() {
        $('#createUserForm').submit();
    };

    var bootstrap = function() {
        $("#createUserForm").formly({'onBlur':false, 'theme':'Light'});
        $("#submitNewUser").click(submitForm);
    };

    $(bootstrap);
};

$(document).ready(function() {
    new $.CreateUser();
});