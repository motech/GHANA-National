$.CWCEnrollmentForm = function() {
    var bootstrap = function() {
        $("#cwcEnrollmentForm").formly({'onBlur':false, 'theme':'Light'});
        $("#enrollCWC").click(function() {
            if(!validate($('#cwcEnrollmentForm'))) {
                $('#cwcEnrollmentForm').submit();
            }
        });
    };

    var validate = function(cwcEnrollmentForm) {
        formValidator.clearMessages(cwcEnrollmentForm);
        formValidator.validatePhoneNumbers(cwcEnrollmentForm);
        formValidator.validateRequiredFields(cwcEnrollmentForm);
        formValidator.validateDate(cwcEnrollmentForm);
        formValidator.validateDateBefore(cwcEnrollmentForm);
        return formValidator.hasErrors(cwcEnrollmentForm);
    };

    $(bootstrap);
};

$(document).ready(function() {
    new $.CWCEnrollmentForm();
    new Field('countries').hasADependent(new Field('regions').hasADependent(new Field('districts').hasADependent(new Field('sub-districts'))));
    $('#sub-districts').change(function() {
        if($(this).val() != '')
        facilities.show($(this));
        else
        $('#facilities').parent().hide();
    });

    $('input[name = "addHistory"]').change(function() {
        if ($(this).val() == "true") {
            $("#jsCareHistory").show();
        } else {
            $("#jsCareHistory").hide();
        }
    });

    $('input[name = "careHistory"]').change(function(e) {
        if ($(this).val() == "OPV") {
            if (e.target.checked) $("#jsOPV").show(); else $("#jsOPV").hide();
        }

        if ($(this).val() == "VITA_A") {
            if (e.target.checked) $("#jsVitA").show(); else $("#jsVitA").hide();
        }

        if ($(this).val() == "IPTI") {
            if (e.target.checked) $("#jsIPTi").show(); else $("#jsIPTi").hide();
        }

        if ($(this).val() == "YF") {
            if (e.target.checked) $("#jsYF").show(); else $("#jsYF").hide();
        }

        if ($(this).val() == "MEASLES") {
            if (e.target.checked) $("#jsMeasles").show(); else $("#jsMeasles").hide();
        }

        if ($(this).val() == "PENTA") {
            if (e.target.checked) $("#jsPenta").show(); else $("#jsPenta").hide();
        }

        if ($(this).val() == "ROTAVIRUS") {
            if (e.target.checked) $("#jsRotavirus").show(); else $("#jsRotavirus").hide();
        }

        if ($(this).val() == "PNEUMOCOCCAL") {
            if (e.target.checked) $("#jsPneumococcal").show(); else $("#jsPneumococcal").hide();
        }

        if ($(this).val() == "BCG") {
            if (e.target.checked) $("#jsBCG").show(); else $("#jsBCG").hide();
        }
    });

    $('#countries').trigger('change');
    if($('input[name = "addHistory"]:checked')) {
        $('input[name = "addHistory"]:checked').trigger('change');
    }

    if($('input[name = "careHistory"]:checked')) {
        $('input[name = "careHistory"]:checked').trigger('change');
    }

    $('#registrationDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#bcgDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#lastOPVDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#lastIPTiDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#vitADate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#measlesDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#yfDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#lastPentaDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#lastRotavirusDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#lastPneumococcalDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
});