$( document ).ready(function() {
    $("#hybridUrl").keypress(function(e){
        if(e.which == 13) {
            openFunction();
        }
    });
});

function openFunction(){
  var url = $('#hybridUrl').val();
  $.ajax(
    {
      url:"open?url="+escape(url), success: function(result){
        result = JSON.parse(result);
        console.log(result);
      }
    }
  );
}
