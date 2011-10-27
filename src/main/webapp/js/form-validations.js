var formValidator = new FormValidator();
function FormValidator() {
    this.validatePhoneNumbers = function(form) {
        var isValid = true;
        form.find('.jsPhone').each(function() {
          if(this.value != "" && !utilities.isPhoneNoValid(this.value)) {
                $("#" + this.id + "Error").show();
                isValid = false;
                return false;
            }
        });
        return isValid;
    }
}