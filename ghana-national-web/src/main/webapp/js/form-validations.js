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
            var val = $(this).val();
            if($(this).attr('type') == 'radio') {
                val = $("input[name='" + this.name + "']:checked").val();
            }
            if(utilities.isNull(val)) {
                $("#" + $(this).attr('id') + "Error").removeClass('hide');
            }
        });
    }

    this.hasErrors = function(form) {
        return form.find('.alertText').is(':visible');
    }

    this.clearMessages = function(form) {
        form.find('.alertText').addClass('hide');
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
                $("#" + this.name + "DateError").removeClass('hide');
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
}