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
        var self = this;
        form.find('.jsRequire').each(function() {
            self.validateFieldForMandatoryValue(this);
        });
    }
    
    this.validateFieldForMandatoryValue = function(field) {
        //select & text
        var val = $(field).val();
        var fieldName = $(field).attr("id");

        //radio & checkbox
        if($(field).attr('type') == 'radio' || $(field).attr('type') == 'checkbox') {
            fieldName = utilities.escapeDot(field.name);
            val = $("input[name='" + fieldName + "']:checked").val();
        }
        if(utilities.isNull(val)) {
             $("#" + fieldName + "Error").removeClass('hide');
        }
    }

    this.hasErrors = function(form) {
        return form.find('.alertText').is(':visible');
    }

    this.clearMessages = function(form) {
        form.find('.alertText').each(function() {
            $(this).addClass('hide');
        });
        form.find('input, textarea').each(function() {
            $(this).val(jQuery.trim($(this).val()));
        });
    }

    this.validateDate = function(form) {
        form.find('.jsDate').each(function() {
            var val = $(this).val();
            var re = /\b\d{1,2}[\/]\d{1,2}[\/]\d{4}\b/;
            if(utilities.isNull(val)) {
                return;
            }

            var valid = true;
            if (!re.test(val)) {
                valid = false;
            }

            if(!valid) {
                $("#" + this.name + "FormatError").removeClass('hide');
            }
        });
    }

    this.validateEmail = function(form) {
        form.find('.jsEmail').each(function() {
            var val = $(this).val();
            var re = /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i;
            var valid = true;
            if(utilities.isNull(val)) {
                valid = false;
            }

            if (!re.test(val)) {
                valid = false;
            }

            if(!valid) {
                $("#" + this.name + "Error").removeClass('hide');
            }
        });
    }

    this.validateDateBefore = function(form) {
       form.find('.jsBefore').each(function() {
            var val = $(this).val();

            if(utilities.isNull(val)) {
                return;
            }

            var delim1 = val.indexOf("/");
            var delim2 = val.lastIndexOf("/");
            var day = parseInt(val.substring(0, delim1), 10);
            var month = parseInt(val.substring(delim1 + 1, delim2), 10);
            var year = parseInt(val.substring(delim2 + 1), 10);
            var givenDate = new Date();
            givenDate.setFullYear(year, month - 1, day);
            var valid = true;
            if(isNaN(givenDate.getDate()) || givenDate > new Date()) {
                valid = false;
            }

            if(!valid) {
                $("#" + this.name + "DateError").removeClass('hide');
            }
       });
    };

    this.validateNumber = function(form) {
          form.find('.jsNumber').each(function() {
            if(this.value != "" && !utilities.isNumber(this.value)) {
              $("#" + this.id + "Error").removeClass('hide');
            }
          });
    };

    this.validateDateAfter = function(form) {
       form.find('.jsAfter').each(function() {
            var val = $(this).val();

            if(utilities.isNull(val)) {
                return;
            }

            var delim1 = val.indexOf("/");
            var delim2 = val.lastIndexOf("/");
            var day = parseInt(val.substring(0, delim1), 10);
            var month = parseInt(val.substring(delim1 + 1, delim2), 10);
            var year = parseInt(val.substring(delim2 + 1), 10);
            var givenDate = new Date();
            givenDate.setFullYear(year, month - 1, day);
            var valid = true;
            if(isNaN(givenDate.getDate()) || givenDate < new Date()) {
                valid = false;
            }

            if(!valid) {
                $("#" + this.name + "DateError").removeClass('hide');
            }
       });
    };

    this.validateRegEx = function (regEx, fieldValue, errorMessage, errorField) {
        if (!regEx.test(fieldValue)) {
            errorField.html(errorMessage);
            errorField.removeClass('hide');
            return false;
        }
        return true;
    };
}