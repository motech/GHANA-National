var facilities = (function() {
    var availableFacilities;
    return {
        initializeSelectBoxWithTheCompleteFacilityList : function() {
            if (!availableFacilities) {
                availableFacilities = $('#facilities').html();
            }
            $('#facilities').html(availableFacilities);
        },
        show : function (element) {
            if (!$('#facilities')) {
                return;
            }
            if (element.find('option:selected').attr('parent') != 'select') {
                facilities.initializeSelectBoxWithTheCompleteFacilityList();
                var options = $('#facilities').find('option');
                for (var indx = 0; indx < options.length; indx++) {
                    var optionValue = $(options[indx]).val();
                    if ($(options[indx]).attr('parent') != element.find('option:selected').text()) {
                        $('#facilities option[value=' + optionValue + ']').remove();
                    }

                }
                $('#facilities').prepend('<option value="" parent="select">Select Facility</option>');
                $('#facilities').find('option:first').html('Select Facility');
                $('#facilities').parent().show();
            }
        },
        hide :  function (field) {
            if ($('#facilities')) {
                $('#facilities').find('option:first').html('Select Facility');
                $('#facilities').parent().hide();
            }
        }
    }
}());

var Field = function(elementId) {
    this.id = elementId;
    this.node = $('#' + this.id);
    this.alertNode = $('#' + this.id + 'Error');
    this.options = this.node.find('option');
};

Field.prototype.hideAlert = function() {
    this.alertNode.hide();
};

Field.prototype.hasADependent = function(dependent) {
    this.dependent = dependent;
    var field = this;
    this.node.change(function() {
        field.hideAlert();
        field.populateDependentWithOriginalValues();
        field.showOrHideDependsBasedOnSelection();
    });
    return this;
};

Field.prototype.populateDependentWithOriginalValues = function() {
    var selectedDependentValue = getDependentSelectedText(this.dependent).val();
    this.dependent.node.html(this.dependent.options);
    this.dependent.node.val(selectedDependentValue);
};

Field.prototype.showOrHideDependsBasedOnSelection = function() {
    facilities.hide(this);
    var dependentOptions = getDependentOptionsForSelectedValue(this);
    if (dependentOptions != undefined && dependentOptions.length > 0) {
        this.showDependent(dependentOptions)
    } else {
        if (this.dependent != undefined)
            this.hideDependents();
        facilities.show(this.node);
    }
};

var getDependentOptionsForSelectedValue = function(field) {
    if (field.hasDependent()) {
        return field.dependent.node.find('option').filter(function() {
            return $(this).attr('parent') == field.node.find('option:selected').text() && $(this).text() != '';
        });
    }
};

Field.prototype.hasDependent = function() {
    return this.dependent != undefined;
};

Field.prototype.hideDependents = function() {
    var field = this.dependent;
    if (field != undefined) {
        field.hideAlert();
        field.node.val('');
        field.node.parent().hide();
        if (field.hasDependent())
            field.hideDependents();
    }
};

Field.prototype.getDefaultValue = function() {
    return this.node.find('option[parent=select]');
};

var getDependentSelectedText = function(field) {
    return field.node.find('option').filter(function() {
        return $(this).attr('selected') != undefined;
    });
};

Field.prototype.showDependent = function(dependentOptions) {
    var field = this;
    if (dependentOptions.length > 0 && field.hasDependent()) {
        var dependent = field.dependent;
        var dependentDefaultOption = dependent.getDefaultValue();
        var selectedDependentValue = getDependentSelectedText(dependent).val();
        dependent.node.html(dependentOptions);
        dependent.node.prepend(dependentDefaultOption);
        dependent.node.val(selectedDependentValue);
        dependent.node.parent().show();
        dependent.showOrHideDependsBasedOnSelection();
    }
};
