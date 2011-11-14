$.UserForm = function() {
    var submitForm = function() {
            $('#searchUserForm').submit();
    };
    var bootstrap = function() {
        $("#searchUserForm").formly({'onBlur':false, 'theme':'Light'});
        $("#searchUser").click(submitForm);
    };
    $(bootstrap);
};

$(document).ready(function() {
    new $.UserForm();
});