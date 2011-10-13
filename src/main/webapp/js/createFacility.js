var Field = function(elementId) {
this.id = elementId;
    this.node = $('#' + this.id)
    this.options = this.node.find('option');
}

Field.prototype.hasADependent = function(dependent) {
    this.dependent = dependent;
    var field = this;
    this.node.change(function() {
        field.populateDependentWithOriginalValues();
        field.showOrHideDependsBasedOnSelection();
    });
    return this;
}

Field.prototype.populateDependentWithOriginalValues = function() {
    this.dependent.node.html(this.dependent.options);
}

Field.prototype.showOrHideDependsBasedOnSelection = function() {
    var field = this;
    function getDependentOptionsForSelectedValue() {
        return field.dependent.node.find('option').filter(function() {return this.value == field.node.find('option:selected').text()})
    }

    var dependentOptions = getDependentOptionsForSelectedValue();
    if(dependentOptions.length > 0) {
        this.showDependent(dependentOptions);
    }else {
        this.hideDependents();
    }
}

Field.prototype.hasDependent = function() {
    return this.dependent != undefined;
}

Field.prototype.hideDependents = function() {
    var field = this;
    while(field.hasDependent()) {
        var field = field.dependent;
        field.node.parent().hide();
    }
}

Field.prototype.getDefaultValue = function() {
    return this.node.find('option[value="0"]');
}

Field.prototype.showDependent = function(dependentOptions) {
    if(this.hasDependent()) {
        this.dependent.hideDependents();
        var dependentDefaultOption = this.dependent.getDefaultValue();
        this.dependent.node.html(dependentOptions);
        this.dependent.node.prepend(dependentDefaultOption);
        this.dependent.node.val(dependentDefaultOption.val());
        this.dependent.node.parent().show();
    }
}

$(document).ready(function() {
    $("#createFacilityForm").formly({'onBlur':false, 'theme':'Light'});
    new Field('countries').hasADependent(new Field('regions').hasADependent(new Field('districts').hasADependent(new Field('sub-districts'))));
});

