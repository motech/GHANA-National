$.Utilities = function () {

    var isPhoneNumberValid = function(phoneNumber){
        // Phone Number for a facility should start with zero and should have 10 digits
        return (( phoneNumber.length ==10) ? phoneNumber.match(/^0[0-9]{9,10}$/) :false);
    }
};

$(document).ready(function() {

    new $.Utilities();
});
