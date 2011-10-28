var formValidator = new FormValidator();
function FormValidator() {
    this.validatePhoneNumbers = function(form) {
        form.find('.jsPhone').each(function() {
          if(this.value != "" && !utilities.isPhoneNoValid(this.value)) {
            $("#" + this.id + "Error").removeClass('hide');
          }
        });
    },

    this.validateRequiredFields = function(form) {
        form.find('.jsRequire').each(function() {
            var val = this.value;
            if($(this).attr('type') == 'radio') {
                val = $("input[@name='" + this.name + "']:checked").val();
            }
            if(utilities.isNull(val)) {
                $("#" + this.name + "Error").removeClass('hide');
            }
        });
    }

    this.hasErrors = function(form) {
        return form.find('.alertText').is(':visible');
    }

    this.clearMessages = function(form) {
        form.find('.alertText').addClass('hide');
    }
}