$( document ).ready(function() {
    $("#url").keypress(function(e){
        if(e.which == 13) {
            openFunction();
        }
    });
});

function openFunction(){
  var url = $('url').val();

  $.ajax(
    {
      url:"open?url="+escape(url), success: function(result){
        result = JSON.parse(result);
        console.log(result);
      }
    }
  );
}
