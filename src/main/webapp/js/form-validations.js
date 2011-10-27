var formValidator = new FormValidator();
function FormValidator() {
    this.validatePhoneNumbers = function(form) {
        form.find('.jsPhone').each(function() {
          if(this.value != "" && !utilities.isPhoneNoValid(this.value)) {
            $("#" + this.id + "Error").show();
          }
        });
    },

    this.validateRequiredFields = function(form) {
        form.find('.jsRequire').each(function() {
          if(utilities.isNull(this.value)) {
            $("#" + this.id + "Error").show();
          }
        });
    }

    this.hasErrors = function(form) {
        return form.find('.formlyInvalid').is(':visible');
    }
}