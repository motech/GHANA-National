var phoneOwnership = utilities.lazyLoad(
    function(){
        var instance = $('#phoneOwnership')
        return {
            selectedValue : function(){
                return instance.find('option:selected').val();
            },
            onChangePopulateMediums : function(medium, allMediumOptions){
                instance.change(function(){
                    medium.filterOptions(phoneOwnership.instance().selectedValue(), allMediumOptions);
                });
            },
            onChangePopulateLanguage : function(language, medium, allLanguageOptions){
                instance.change(function(){
                    language.filterOptions(phoneOwnership.instance().selectedValue(), medium.selectedValue(), allLanguageOptions);
                });
            }
        }
    }
);

var medium = utilities.lazyLoad(
    function(){
        var instance = $('#medium');
        return {
            selectedValue : function(){
                return instance.find('option:selected').val();
            },
            html : function(value){
                instance.html(value);
            },
            assignOptions : function(options, selectOption){
                instance.html(options);
                instance.prepend(selectOption);
                instance.val(selectOption);
            },
            filterOptions : function(phoneOwnershipMatchValue, allMediumOptions){
                instance.html(allMediumOptions);
                var filteredOptions = instance.find('option').filter(function(){
                    return $(this).attr('phoneownership') == phoneOwnershipMatchValue;
                });
                medium.instance().assignOptions(filteredOptions, allMediumOptions[0]);
            },
            onChangePopulateLanguage : function(language, phoneOwnership, allLanguageOptions){
                instance.change(function(){
                    language.filterOptions(phoneOwnership.selectedValue(), medium.instance().selectedValue(), allLanguageOptions);
                });
            },
            onChangeShowChoiceOfDayAndTimeForVoice : function() {
                instance.change(function(selection) {

                     if($(this).val() == 'VOICE') {
                        $('#dayOfWeekRow').show();
                        $('#timeOfDayRow').show();
                     } else {
                        $('#dayOfWeekRow').hide();
                        $('#timeOfDayRow').hide();
                     }
                });
            }
        }
    }
);

var language = utilities.lazyLoad(
    function(){
        var instance = $('#language');
        return{
            html : function(value){
                instance.html(value);
            },
            assignOptions : function(options, selectOption){
                instance.html(options);
                instance.prepend(selectOption);
                instance.val(selectOption);
            },
            filterOptions : function(phoneOwnershipMatchValue, mediumMatchValue, allLanguageOptions){
                instance.html(allLanguageOptions);
                var filteredOptions = instance.find('option').filter(function(){
                    return $(this).attr('phoneownership') == phoneOwnershipMatchValue && $(this).attr('medium') == mediumMatchValue;
                });
                language.instance().assignOptions(filteredOptions, allLanguageOptions[0]);
            }

        }
    }
);

var messageStartWeek = utilities.lazyLoad(
    function(){
        var instance = $('#messageStartWeek');
        return {
            assignOptions : function(options, selectOption){
                instance.html(options);
                instance.prepend(selectOption);
                instance.val(selectOption);
            },
            filterOptions : function(serviceTypeMatchValue, allMessageStartWeeksOptions){
                instance.html(allMessageStartWeeksOptions);
                var filteredOptions = instance.find('option').filter(function(){
                    return $(this).attr('servicetype') == serviceTypeMatchValue;
                });
                messageStartWeek.instance().assignOptions(filteredOptions, allMessageStartWeeksOptions[0]);
            }
        }
    }
);

var serviceType = utilities.lazyLoad(
    function(){
        var instance = $('#serviceType');
        return{
            onChangePopulateMessageStartWeek : function(messageStartWeek, allMessageStartWeeksOptions){
                instance.change(function(){
                    messageStartWeek.filterOptions(instance.find('option:selected').val(), allMessageStartWeeksOptions);
                });
            }
        }
    }
);

var consent = utilities.lazyLoad(
    function(){
        var instance = $('input[name=consent]');
        var idsOfFieldsDependentToConsent = ['serviceType', 'phoneOwnership', 'phoneNumber', 'medium', 'dayOfWeek', 'timeOfDay\\.hour', 'timeOfDay\\.minute', 'language',
            'learnedFrom', 'reasonToJoin', 'messageStartWeek'];
        return{
            validateDependentMandatoryFields : function(){
                if($('input[name=consent]:checked').val() == 'true'){
                    $.each(idsOfFieldsDependentToConsent, function(index, id){
                        formValidator.validateFieldForMandatoryValue($('#' + id)[0]);
                    });
                }
            },
            handleDependentFields : function(){
                instance.change(function(){

                    var clearAndHideField = function(id){
                        var field = $('#' + id);
                        if(field.is('select')){
                            field.val(field.find('option[value=]'));
                        }
                        else if(field.is('input')){
                            field.val('');
                        }

                        field.parent().removeClass('hide').addClass('hide');//hide();
                        $('#' + id + 'Error').removeClass('hide').addClass('hide'); //hide();
                    }

                    var showFieldsDependentOnConsent = function(){
                        $.each(idsOfFieldsDependentToConsent, function(index, id){
                            $('#' + id).parent().removeClass('hide');
                        });
                    }

                    var clearAndHideFieldsDependentOnConsent = function(){
                        $.each(idsOfFieldsDependentToConsent, function(index, id){
                            clearAndHideField(id);
                        });
                    }

                    var consent = $('input[name=consent]:checked').val();
                    if(consent == 'true'){
                        showFieldsDependentOnConsent();
                    }else{
                        clearAndHideFieldsDependentOnConsent();
                    }

                });
            }
        }
    }
);

$(document).ready(function() {

    $("#mobileMidwifeEnrollmentForm").formly({'onBlur':false, 'theme':'Light'});

    var validate = function(form){
        formValidator.clearMessages(form);
        formValidator.validatePhoneNumbers(form);
        formValidator.validateRequiredFields(form);
        consent.instance().validateDependentMandatoryFields();

        return formValidator.hasErrors(form);
    }

    consent.instance().handleDependentFields();

    var initialLanguageOptions = $('#language').find('option');
    phoneOwnership.instance().onChangePopulateLanguage(language.instance(), medium.instance(), initialLanguageOptions);
    medium.instance().onChangePopulateLanguage(language.instance(), phoneOwnership.instance(), initialLanguageOptions);
    medium.instance().onChangeShowChoiceOfDayAndTimeForVoice();

    var initialMediumOptions = $('#medium').find('option');
    phoneOwnership.instance().onChangePopulateMediums(medium.instance(), initialMediumOptions);

    var initialServiceTypeOptions = $('#messageStartWeek').find('option');
    serviceType.instance().onChangePopulateMessageStartWeek(messageStartWeek.instance(), initialServiceTypeOptions);

    $("input[name=consent]:checked").trigger('change');

    (function selectServiceTypeToFilterMessageStartWeek_AndFillSelectedValue () {
        $('#serviceType').trigger('change');
        $('#messageStartWeek').val($("#messageStartWeekSelected").val()).trigger('change');
    })();

    (function triggerPhoneOwnershipAndSetMediumAndLanguageToFilterDropDownValue_AndFillSelectedValue () {
        $('#phoneOwnership').trigger('change');
        // previously selected values is reselected via hidden params - for medium, location, messageStartWeek
        $('#medium').val($("#mediumSelected").val()).trigger('change');
        $('#language').val($("#languageSelected").val());        
    })();

    $("#phoneOwnership").change(function() {
        if ($(this).val() === 'PUBLIC') {
            $('#phoneNumber').removeClass('jsRequire');
        } else {
            $('#phoneNumber').addClass('jsRequire');
        }
    });

    $('#submitMobileMidwife').click(function() {
        var form = $('#mobileMidwifeEnrollmentForm');
        if (!validate(form)){
            form.submit();
        }
    });
});

