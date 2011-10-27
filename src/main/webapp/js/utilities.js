var utilities = new Utilities();
function Utilities() {
    this.isPhoneNoValid = function(phoneNo) {
        return (( phoneNo.length == 10) ? phoneNo.match(/^0[0-9]*/) : false);
    },

    this.isNotNull = function(id) {
        return $(id).val().length == 0;
    },

    this.clearMessages = function() {
        $('.alertText').hide();
    }
}