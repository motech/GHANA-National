$.ANCEnrollmentForm = function() {
    var heightRegEx = /^[0-9]+[\.]?[0-9]+$/;
    var gravidaRegEx = /^[0-9]+$/;
    var parityRegEx = /^[0-9]+$/;
    var submitForm = function() {
        var form = $("#ancEnrollmentForm");
        if (!validate(form))
            form.submit();
    };

    var bootstrap = function() {
        $("#ancEnrollmentForm").formly({'onBlur':false, 'theme':'Light'});
        $("#submitANC").click(submitForm);
    };
    $(bootstrap);

    var validate = function(form) {
        formValidator.clearMessages(form);
        formValidator.validateRequiredFields(form);
        formValidator.validateDateBefore(form);
        var hasError = validateHeight() || validateGravida() || validateParity();
        return hasError || formValidator.hasErrors(form);
    };

    var validateHeight = function () {
        var height = $('#height').val();
        var errorMessage = "Height is invalid";
        var isNumber = formValidator.validateRegEx(heightRegEx, height, errorMessage, $('#heightError'));
        if (isNumber && !(height >= 60 && height <= 200)) {
            $('#heightError').html(errorMessage);
            $('#heightError').removeClass('hide');
            return true;
        }
        return false;
    };

    var validateGravida = function () {
        var gravida = $('#gravida').val();
        var errorMessage = "Gravida is invalid";
        var isNumber = formValidator.validateRegEx(gravidaRegEx, gravida, errorMessage, $('#gravidaError'));
        if (isNumber && !(gravida >= 1 && gravida <= 30)) {
            $('#gravidaError').html(errorMessage);
            $('#gravidaError').removeClass('hide');
            return true;
        }
        return false;
    };

    var validateParity = function () {
        var parity = $('#parity').val();
        var errorMessage = "Parity is invalid";
        var isNumber = formValidator.validateRegEx(parityRegEx, parity, errorMessage, $('#parityError'));
        if (isNumber && !(parity >= 0 && parity <= 30)) {
            $('#parityError').html(errorMessage);
            $('#parityError').removeClass('hide');
            return true;
        }
        return false;
    };
};

$(document).ready(function() {
    new $.ANCEnrollmentForm();
    new Field('countries').hasADependent(new Field('regions').hasADependent(new Field('districts').hasADependent(new Field('sub-districts'))));
    $('#sub-districts').change(function() {
        facilities.show($(this));
    });

    $('input[name = "addHistory"]').change(function() {
        if ($(this).val() == "true") {
            $("#jsCareHistory").show();
        } else {
            $("#jsCareHistory").hide();
        }
    });

    $('input[name = "careHistory"]').change(function(e) {
        if ($(this).val() == "IPT_SP") {
            if (e.target.checked) $("#jsIPT").show(); else $("#jsIPT").hide();
        }

        if ($(this).val() == "TT") {
            if (e.target.checked) $("#jsTT").show(); else $("#jsTT").hide();
        }
    });

    $('#regions').trigger('change');
    $('input').trigger('change');

    $('#registrationDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#estimatedDateOfDelivery').datepicker({dateFormat: "dd/mm/yy", maxDate: '+1y', buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '-1:+1:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#lastTTDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#lastIPTDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});


    if($("input[name='addHistory']:checked").val() == "true") {
        $('#addHistory1').trigger('change');

        if($("input[name='lastTT']:checked").val() != undefined)
            $('#careHistory1').attr('checked','checked');
        if($("input[name='lastIPT']:checked").val() != undefined)
            $('#careHistory2').attr('checked','checked');

        if($('#careHistory1').attr('checked') == 'checked')
           $('#careHistory1').trigger('change');
        if($('#careHistory2').attr('checked') == 'checked')
           $('#careHistory2').trigger('change');
    }
});
