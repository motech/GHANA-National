$(document).ready(function() {
    $("#mobileMidwifeEnrollmentForm").formly({'onBlur':false, 'theme':'Light'});

    $('#submit').click(function() {
        var mobileMidwifeEnrollmentForm = $('#mobileMidwifeEnrollmentForm');
        alert("sdf");
        mobileMidwifeEnrollmentForm.submit();
    });
});

