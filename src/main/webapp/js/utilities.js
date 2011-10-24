$.Utilities = function() {
    this.isPhoneNoValid = function(phoneNo) {
        return (( phoneNo.length == 10) ? phoneNo.match(/^0[0-9]*/) : false);
    }
}

$(document).ready(function() {
    new $.Utilities();
});