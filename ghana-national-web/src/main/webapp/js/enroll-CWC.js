$.CWCEnrollmentForm = function() {
    var submitForm = function() {
        $('#cwcEnrollmentForm').submit();
    };
    var bootstrap = function() {
        $("#cwcEnrollmentForm").formly({'onBlur':false, 'theme':'Light'});
        $("#submit").click(submitForm);
    };
    $(bootstrap);
};

$(document).ready(function() {
    new $.CWCEnrollmentForm();
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

        if ($(this).val() == "BCG") {
            if (e.target.checked) $("#jsBCG").show(); else $("#jsBCG").hide();
        }
    });

    $('#regions').trigger('change');
    $('#registrationDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#bcgDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#lastOPVDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#lastIPTiDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#vitADate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#measlesDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#yfDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, yearRange: '1900:', buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
});