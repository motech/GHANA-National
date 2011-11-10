var utilities = new Utilities();
function Utilities() {
    this.isPhoneNoValid = function(phoneNo) {
        return ((!phoneNo.match(/[^\d]+/) && phoneNo.charAt(0) == '0') ? phoneNo.length == 10 : false);
    },

    this.isNull = function(value) {
        return value == undefined || $.trim(value) == "";
    }
}