var facilities = (function(){
    var availableFacilities;
    return {
         initializeSelectBoxWithTheCompleteFacilityList : function(){
            if(!availableFacilities){
                availableFacilities = $('#facilities').html();
            }
            $('#facilities').html(availableFacilities);
         },
         show : function (element) {
            if(!$('#facilities')) {
                return;
            }
            if(element.find('option:selected').attr('parent') != 'select') {
                facilities.initializeSelectBoxWithTheCompleteFacilityList();
                var facilitySelect = $('#facilities');
                var selectedFacility = facilitySelect.find('option:selected').val();
                facilitySelect.html($('#facilities').find('option').filter(function() {
                    return ($(this).attr('parent') == element.find('option:selected').text());
                }));
                facilitySelect.prepend('<option value="" parent="select">Select Facility</option>');
                facilitySelect.val(selectedFacility);
                facilitySelect.parent().show();
            }
        },
        hide :  function (field) {
            if($('#facilities')) {
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
}

Field.prototype.hideAlert = function() {
    this.alertNode.hide();
}

Field.prototype.hasADependent = function(dependent) {
    this.dependent = dependent;
    var field = this;
    this.node.change(function() {
        field.hideAlert();
        var previouslySelectedDependentValue = field.dependent.node.find('option:selected').text();
        field.populateDependentWithOriginalValues();
        field.showOrHideDependsBasedOnSelection(previouslySelectedDependentValue);
    });
    return this;
}

Field.prototype.populateDependentWithOriginalValues = function() {
    this.dependent.node.html(this.dependent.options);
}

Field.prototype.showOrHideDependsBasedOnSelection = function(previouslySelectedDependentValue) {
    var field = this;
    function getDependentOptionsForSelectedValue() {
        return field.dependent.node.find('option').filter(function() {
            return $(this).attr('parent') == field.node.find('option:selected').text() && $(this).text() != '';
        });
    }

    var dependentOptions = getDependentOptionsForSelectedValue();
    facilities.hide(field);
    if (dependentOptions.length > 0) {
        this.showDependent(dependentOptions);
        $(field.dependent.node).val(previouslySelectedDependentValue).trigger('change');
    } else {
        this.hideDependents();
        facilities.show(field.node);
    }
}

Field.prototype.hasDependent = function() {
    return this.dependent != undefined;
}

Field.prototype.hideDependents = function() {
    var field = this;
    while (field.hasDependent()) {
        var field = field.dependent;
        field.node.parent().hide();
//        field.node.find('option:first').attr('selected', 'selected');
    }
}

Field.prototype.getDefaultValue = function() {
    return this.node.find('option[parent=select]');
}

Field.prototype.showDependent = function(dependentOptions) {
    if (this.hasDependent()) {
        this.dependent.hideAlert();
        this.dependent.hideDependents();
        var dependentDefaultOption = this.dependent.getDefaultValue();
        this.dependent.node.html(dependentOptions);
        this.dependent.node.prepend(dependentDefaultOption);
        this.dependent.node.find('option:first').attr('selected', 'selected');
        this.dependent.node.parent().show();
    }
}