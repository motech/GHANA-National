var utilities = new Utilities();
function Utilities() {
    this.isPhoneNoValid = function(phoneNo) {
        return (( phoneNo.length == 10) ? phoneNo.match(/^0[0-9]*/) : false);
    },

    this.isNull = function(value) {
        return value == undefined || $.trim(value) == "";
    }
}