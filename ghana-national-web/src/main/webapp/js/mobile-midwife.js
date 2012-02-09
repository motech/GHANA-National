var formElements = function() {
    return {
        show : function(elemId) {
            if (consent.instance().isFieldDependentOnConsent(elemId)) {
                if (consent.instance().isConsentYes())
                    $(elemId).show();
            } else {
                $(elemId).show();
            }
        },
        showTimeOfDayAndDayOfWeek : function () {
            this.show('#timeOfDayRow');
            $("#timeOfDayHour").removeAttr('disabled');
            $("#timeOfDayMinute").removeAttr('disabled');

            this.show('#dayOfWeekRow');
            $("#dayOfWeek").removeAttr('disabled');
        },
        hideTimeOfDayAndDayOfWeek : function () {
            $('#timeOfDayRow').hide();
            $("#timeOfDayHour").val('').attr('disabled', true);
            $("#timeOfDayMinute").val('').attr('disabled', true);

            $('#dayOfWeekRow').hide();
            $("#dayOfWeek").val('').attr('disabled', true);
        },
        clearValue : function (id) {
            var field = $('#' + id);
            if (field.is('select')) {
                field.val(field.find('option[value=]'));
            }
            else if (field.is('input')) {
                field.val('');
            }
        }
    }
}();

var phoneOwnership = utilities.lazyLoad(
        function() {
            var instance = $('#phoneOwnership');
            return {
                selectedValue : function() {
                    return instance.find('option:selected').val();
                },
                onChangePopulateMediums : function(medium, allMediumOptions) {
                    instance.change(function() {
                        medium.filterOptions(phoneOwnership.instance().selectedValue(), allMediumOptions);
                    });
                },
                onChangePopulateLanguage : function(language, medium, allLanguageOptions) {
                    instance.change(function() {
                        language.filterOptions(phoneOwnership.instance().selectedValue(), medium.selectedValue(), allLanguageOptions);
                    });
                },
                onChangeToPublicOwnershipDisablePhoneFields : function() {
                    instance.change(function() {
                        if ($('#phoneOwnership').val() === 'PUBLIC') {
                            $('#phoneNumberRow').hide();
                            $('#phoneNumber').val('');
                            formElements.hideTimeOfDayAndDayOfWeek();
                        } else if ($('#medium').val() == 'VOICE') {
                            formElements.show('#phoneNumberRow');
                            formElements.showTimeOfDayAndDayOfWeek();
                        }
                    });
                }
            }
        }
        );

var medium = utilities.lazyLoad(
        function() {
            var instance = $('#medium');
            return {
                selectedValue : function() {
                    return instance.find('option:selected').val();
                },
                html : function(value) {
                    instance.html(value);
                },
                assignOptions : function(options, selectOption) {
                    instance.html(options);
                    instance.prepend(selectOption);
                    instance.val(selectOption);
                },
                filterOptions : function(phoneOwnershipMatchValue, allMediumOptions) {
                    instance.html(allMediumOptions);
                    var filteredOptions = instance.find('option').filter(function() {
                        return $(this).attr('phoneownership') == phoneOwnershipMatchValue;
                    });
                    medium.instance().assignOptions(filteredOptions, allMediumOptions[0]);
                },
                onChangePopulateLanguage : function(language, phoneOwnership, allLanguageOptions) {
                    instance.change(function() {
                        language.filterOptions(phoneOwnership.selectedValue(), medium.instance().selectedValue(), allLanguageOptions);
                    });
                },
                onChangeShowChoiceOfDayAndTimeForVoice : function() {
                    instance.change(function(selection) {

                        if ($(this).val() == 'VOICE' && $('#phoneOwnership').val() != 'PUBLIC') {
                            formElements.showTimeOfDayAndDayOfWeek();
                        } else {
                            formElements.hideTimeOfDayAndDayOfWeek()
                        }
                    });
                }
            }
        }
        );

var language = utilities.lazyLoad(
        function() {
            var instance = $('#language');
            return{
                html : function(value) {
                    instance.html(value);
                },
                assignOptions : function(options, selectOption) {
                    instance.html(options);
                    instance.prepend(selectOption);
                    instance.val(selectOption);
                },
                filterOptions : function(phoneOwnershipMatchValue, mediumMatchValue, allLanguageOptions) {
                    instance.html(allLanguageOptions);
                    var filteredOptions = instance.find('option').filter(function() {
                        return $(this).attr('phoneownership') == phoneOwnershipMatchValue && $(this).attr('medium') == mediumMatchValue;
                    });
                    language.instance().assignOptions(filteredOptions, allLanguageOptions[0]);
                }

            }
        }
        );

var messageStartWeek = utilities.lazyLoad(
        function() {
            var instance = $('#messageStartWeek');
            return {
                assignOptions : function(options, selectOption) {
                    instance.html(options);
                    instance.prepend(selectOption);
                    instance.val(selectOption);
                },
                filterOptions : function(serviceTypeMatchValue, allMessageStartWeeksOptions) {
                    instance.html(allMessageStartWeeksOptions);
                    var filteredOptions = instance.find('option').filter(function() {
                        return $(this).attr('servicetype') == serviceTypeMatchValue;
                    });
                    messageStartWeek.instance().assignOptions(filteredOptions, allMessageStartWeeksOptions[0]);
                }
            }
        }
        );

var serviceType = utilities.lazyLoad(
        function() {
            var instance = $('#serviceType');
            return{
                onChangePopulateMessageStartWeek : function(messageStartWeek, allMessageStartWeeksOptions) {
                    instance.change(function() {
                        messageStartWeek.filterOptions(instance.find('option:selected').val(), allMessageStartWeeksOptions);
                    });
                }
            }
        }
        );

var consent = utilities.lazyLoad(
        function() {
            var consentElement = $('input[name=consent]');
            var idsOfFieldsDependentToConsent = ['serviceType', 'phoneOwnership', 'phoneNumber', 'medium', 'dayOfWeek', 'timeOfDayHour', 'timeOfDayMinute', 'language',
                'learnedFrom', 'reasonToJoin', 'messageStartWeek'];
            consentElement.isConsentYes = function () {
                return $('input[name=consent]:checked').val() == 'true';
            };
            return {
                validateDependentMandatoryFields : function() {
                    if ($('input[name=consent]:checked').val() == 'true') {
                        $.each(idsOfFieldsDependentToConsent, function(index, id) {
                            if (id === "phoneNumber" && $('#phoneOwnership').val() == 'PUBLIC')
                                return;
                            formValidator.validateFieldForMandatoryValue($('#' + id)[0]);
                        });
                    }
                },
                isFieldDependentOnConsent : function (elemId) {
                    return $.inArray(elemId, idsOfFieldsDependentToConsent);
                },
                isConsentYes : function () {
                    return consentElement.isConsentYes();
                },
                handleDependentFields : function() {
                    consentElement.change(function() {

                        var clearAndHideField = function(id) {
                            var field = $('#' + id);
                            formElements.clearValue(id);
                            field.parent().removeClass('hide').addClass('hide');//hide();
                            $('#' + id + 'Error').removeClass('hide').addClass('hide'); //hide();
                        };

                        var showFieldsDependentOnConsent = function() {
                            $.each(idsOfFieldsDependentToConsent, function(index, id) {
                                $('#' + id).parent().removeClass('hide');
                            });
                        };

                        var clearAndHideFieldsDependentOnConsent = function() {
                            $.each(idsOfFieldsDependentToConsent, function(index, id) {
                                clearAndHideField(id);
                            });
                        };

                        if (consentElement.isConsentYes()) {
                            showFieldsDependentOnConsent();
                        } else {
                            clearAndHideFieldsDependentOnConsent();
                        }

                    });
                }
            }
        }
        );

$(document).ready(function() {
    new Field('countries').hasADependent(new Field('regions').hasADependent(new Field('districts').hasADependent(new Field('sub-districts'))));

    $('#sub-districts').change(function() {
        facilities.show($(this));
    });
    $('#regions').trigger('change');
    $('input').trigger('change');

    $("#mobileMidwifeEnrollmentForm").formly({'onBlur':false, 'theme':'Light'});

    var validate = function(form) {
        formValidator.clearMessages(form);
        formValidator.validatePhoneNumbers(form);
        formValidator.validateRequiredFields(form);
        consent.instance().validateDependentMandatoryFields();

        return formValidator.hasErrors(form);
    }
    consent.instance().handleDependentFields();

    var initialLanguageOptions = $('#language').find('option');
    phoneOwnership.instance().onChangePopulateLanguage(language.instance(), medium.instance(), initialLanguageOptions);
    phoneOwnership.instance().onChangeToPublicOwnershipDisablePhoneFields();
    medium.instance().onChangePopulateLanguage(language.instance(), phoneOwnership.instance(), initialLanguageOptions);
    medium.instance().onChangeShowChoiceOfDayAndTimeForVoice();

    var initialMediumOptions = $('#medium').find('option');
    phoneOwnership.instance().onChangePopulateMediums(medium.instance(), initialMediumOptions);

    var initialServiceTypeOptions = $('#messageStartWeek').find('option');
    serviceType.instance().onChangePopulateMessageStartWeek(messageStartWeek.instance(), initialServiceTypeOptions);

    $("input[name=consent]:checked").trigger('change');

    (function selectServiceTypeToFilterMessageStartWeek_AndFillSelectedValue() {
        $('#serviceType').trigger('change');
        $('#messageStartWeek').val($("#messageStartWeekSelected").val()).trigger('change');
    })();

    (function triggerPhoneOwnershipAndSetMediumAndLanguageToFilterDropDownValue_AndFillSelectedValue() {
        $('#phoneOwnership').trigger('change');
        // previously selected values is reselected via hidden params - for medium, location, messageStartWeek
        $('#medium').val($("#mediumSelected").val()).trigger('change');
        $('#language').val($("#languageSelected").val());
    })();

    $('#submitMobileMidwife').click(function() {
        var form = $('#mobileMidwifeEnrollmentForm');
        if (!validate(form)) {
            form.submit();
        }
    });
});

