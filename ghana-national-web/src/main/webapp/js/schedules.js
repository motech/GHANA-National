$(document).ready(function() {
    $('#startDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, maxDate: new Date(2020, 1, 1), minDate: new Date(1990, 1, 1), buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#endDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, maxDate: new Date(2020, 1, 1), minDate: new Date(1990, 1, 1), buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#regDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, maxDate: new Date(2020, 1, 1), minDate: new Date(1990, 1, 1), buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#patientDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, maxDate: new Date(2020, 1, 1), minDate: new Date(1990, 1, 1), buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    var today = new Date();
    $("#regDate").val(format_to_fixed_length('' + today.getDate(), 2, '0') + today.getDate() + '/' + format_to_fixed_length('' + (today.getMonth() + 1), 2, '0') + (today.getMonth() + 1) + '/' + today.getFullYear());

    $('#patientType').change(function(){
        var selectedValue = $(this).find(":selected").val();
        var patientDateLabel = '';
        if(selectedValue === 'Mother'){
            patientDateLabel = 'EDD :';
        }else if(selectedValue === 'Child' || selectedValue === 'Other'){
            patientDateLabel = 'DOB :'
        }
        $('#patientDateLabel').html(patientDateLabel);
        return false;
    })
});

var format_to_fixed_length = function(string, len, prefix_char){
    if(string.length < len){
        return Array(len - string.length + 1).join(prefix_char);
    }
    return '';
}