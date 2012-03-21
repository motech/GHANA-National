$(document).ready(function() {
    $('#startDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, maxDate: new Date(2020, 1, 1), minDate: new Date(1990, 1, 1), buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
    $('#endDate').datepicker({dateFormat: "dd/mm/yy", maxDate: 0, buttonImageOnly: true, changeYear: true, changeMonth: true, maxDate: new Date(2020, 1, 1), minDate: new Date(1990, 1, 1), buttonImage: '../../resources/images/calendar.gif', showOn: 'both'});
});