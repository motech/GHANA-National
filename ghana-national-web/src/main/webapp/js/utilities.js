var utilities = new Utilities();
function Utilities() {
    this.isPhoneNoValid = function(phoneNo) {
        return ((!phoneNo.match(/[^\d]+/) && phoneNo.charAt(0) == '0') ? phoneNo.length == 10 : false);
    },

    this.isNull = function(value) {
        return value == undefined || $.trim(value) == "";
    },

    this.escapeDot = function(string){
        return string.replace('.', '\\.');
    }

    this.lazyLoad = function(fun){
       var inst;
        return {
		    instance : function() {
			    if(!inst) {
				    inst = fun();
			    }
			    return inst;
		    }
	    }
    }
}

$(document).ready(function() {
    $('#menu ul li').hover(function(){
            $(this).children('ul').css('display','block');
        },
    function() {
        $(this).children('ul').css('display','none');
    });
});