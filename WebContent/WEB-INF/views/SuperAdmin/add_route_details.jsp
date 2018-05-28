<jsp:include page="header.jsp" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="col-md-9 col-sm-9 col-xs-12 m-side">
	<form method="post" name="route" id="route">
			        <input id="hidLat" name="source_lat" type="hidden" value="${route.source_lat}">
                    <input id="hidLong" name="source_lng" type="hidden" value="${route.source_lng}">
                    
                    <input id="hidLat1" name="destination_lat" type="hidden" value="${route.destination_lat}">
                    <input id="hidLong1" name="destination_lng" type="hidden" value="${route.destination_lng}">
                  
                     <input id="school_add" name="school_add" type="hidden" value="${school_details.school_address}">
                    
			<div class="new-student-form">
			<div class="form-group">
			  <label class="col-sm-12 control-label">Route Name :</label>
			    <div class="col-sm-12">
			      <input type="text" name="route_name" value=""  id="route_name" class="form-control">
			     			     </div>
			</div>
			<div class="clear">&nbsp;</div>
			
			<div class="form-group">
			  <label class="col-sm-12 control-label">Note:</label>
			    <div class="col-sm-12">
			      <input type="text" name="note" value=""  id="note" class="form-control">
			     </div>
			</div>
			<div class="clear">&nbsp;</div>
			<div class="form-group">
			  <label class="col-sm-12 control-label">Source:</label>
			    <div class="col-sm-12">
			      <input type="text" name="source" value=""  id="source" class="form-control">
			     </div>
			</div>
			<div class="clear">&nbsp;</div>
				<div class="form-group">
			  		<label class="col-sm-12 control-label">Source Note :</label>
			    	<div class="col-sm-12">
			      		<input type="text" name="source_note" value=""  id="source_note" class="form-control">
			     	</div>
			</div>
			<div class="clear">&nbsp;</div>
			<div class="form-group">
			  <label class="col-sm-12 control-label">School Address as Source:</label>
			    <div class="col-sm-12">
			      <input type="checkbox" onchange="myfunction();" style="width: auto;"  name="school_address"   id="school_address" class="form-control">
			     </div>
			</div>
			<div class="clear">&nbsp;</div>
			   <div class="form-group col-sm-12">
                   	 <div id="geomap" style="width:100%; height:300px;">
            <p>Loading Please Wait...</p>
        </div>
                   </div>
             <div class="clear">&nbsp;</div>
			<div class="form-group">
			  <label class="col-sm-12 control-label">Destination:</label>
			    <div class="col-sm-12">
			      <input type="text" name="destination" value=""  id="destination" class="form-control">
			     </div>
			</div>
			<div class="clear">&nbsp;</div>
				<div class="form-group">
			  		<label class="col-sm-12 control-label">Destination Note :</label>
			    	<div class="col-sm-12">
			      		<input type="text" name="destination_note" value=""  id="destination_note" class="form-control">
			     	</div>
			</div>
			<div class="clear">&nbsp;</div>
			<div class="form-group">
			  <label class="col-sm-12 control-label">School Address as Destination:</label>
			    <div class="col-sm-12">
			      <input type="checkbox" onchange="myfunction1();" style="width: auto;"  name="school_address1"   id="school_address1" class="form-control">
			     </div>
			</div>
				<div class="clear">&nbsp;</div>
			   <div class="form-group col-sm-12">
                   	 <div id="geomap1" style="width:100%; height:300px;">
            <p>Loading Please Wait...</p>
        </div>
                   </div>
			<div class="clear">&nbsp;</div>
		<!-- 	<div class="form-group">
			    <div class="col-sm-9">
			      <input type="submit"  value="Update"  class="btn btn-primary">
			     </div>
			</div>  -->
			<div class="form-group">
	            <div class="col-sm-12">
		             <input  class="btn btn-primary btn-submit" value="Save" type="submit">&nbsp;&nbsp;
		             <a href="admin/allRoute">
		             <input  class="btn btn-primary btn-submit" value="Cancel" type="button">
		             </a>	
	            </div>
			</div>         
			
			</div> 
             
          </div>
         </form>
        <script src="resources/front/js/jquery.validate.min.js"> </script>
  <script>
  $(document).ready(function(){
		$("#route").validate({
          rules: {
        	  route_name: "required",
        	  destination:"required",
        	  source:"required"
          },
          messages: {
        	  route_name: "Please enter route name",
        	  destination:"Please enter destination",
        	  source:"Please enter source"
        	  },
          submitHandler: function(form) {
              form.submit();
          }
      });
      

});
  
  </script>
      <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?libraries=places&sensor=false&key=AIzaSyC7CzVRaX4toLFXcXnNV_8TUtgRy2Y0efE"></script>
  
 <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js"></script>   
 <script type="text/javascript" src="resources/front/js/jquery.geocomplete.min.js"></script>             
 <script type="text/javascript">
        var schoolLocation = new google.maps.LatLng(${school_details.school_lat}, ${school_details.school_lng} );
        var longval = "#hidLong";
        var latval = "#hidLat";
        var geocoder;
        var map;
        var marker;

        function initialize() {
            //MAP
            var initialLat = $(latval).val();
            var initialLong = $(longval).val();
            var latlng = new google.maps.LatLng(initialLat, initialLong);
            var options = {
                zoom: 9,
                center: latlng,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };
        
            map = new google.maps.Map(document.getElementById("geomap"), options);
         
            geocoder = new google.maps.Geocoder();    
        
            marker = new google.maps.Marker({
                map: map,
                draggable: true,
                position: latlng
            });
            var marker1 = new google.maps.Marker({
                position: {lat: ${school_details.school_lat} , lng: ${school_details.school_lng}},
                map: map,
                icon:'resources/dashboard/Images/school_icon.png'
              });
            
        
            google.maps.event.addListener(marker, "dragend", function (event) {
                var point = marker.getPosition();
                map.panTo(point);
            });
            
        };
        
        $(document).ready(function () {
        
            initialize();
        
            $(function () {
                $("#source").autocomplete({
                    source: function (request, response) {
                        geocoder.geocode({ 'address': request.term }, function (results, status) {
                            response($.map(results, function (item) {
                                return {
                                    label: item.formatted_address,
                                    value: item.formatted_address
                                };
                            }));
                        });
                    }
                });
            });
        
           
            
            $('#source').on("change",function (e) {
            	setTimeout(function(){ 
                var address =$("#source").val();
                geocoder.geocode({ 'address': address }, function (results, status) {
                    if (status == google.maps.GeocoderStatus.OK) {
                        map.setCenter(results[0].geometry.location);
                        marker.setPosition(results[0].geometry.location);
                        $(latval).val(marker.getPosition().lat());
                        $(longval).val(marker.getPosition().lng());
                    } else {
                        alert("Geocode was not successful for the following reason: " + status);
                    }
                });
                e.preventDefault();
            	 }, 400);
            });
        
            //Add listener to marker for reverse geocoding
            google.maps.event.addListener(marker, 'drag', function () {
                geocoder.geocode({ 'latLng': marker.getPosition() }, function (results, status) {
                    if (status == google.maps.GeocoderStatus.OK) {
                        if (results[0]) {
                            $("#source").val(results[0].formatted_address);
                            $(latval).val(marker.getPosition().lat());
                            $(longval).val(marker.getPosition().lng());
                        }
                    }
                });
            });
        });
  
    </script>
 <script type="text/javascript">

 function initializeAutoComplete() {
	  var mapOptions = {
	    center: { lat: 51.5072, lng: 0.1275},
	    zoom: 12,
	    mapTypeControl: false,
	    streetViewControl: false,
	    panControl: false,
	    scrollwheel: false,
	    zoomControl: true,
	    zoomControlOptions: {
	    style: google.maps.ZoomControlStyle.SMALL,
	    position: google.maps.ControlPosition.LEFT_TOP}
	  };
	  var map = new google.maps.Map(document.getElementById('map_canvas'),
	    mapOptions);
	  }
	  google.maps.event.addDomListener(window, 'load', initializeAutoComplete);

	 $("#source").geocomplete({}); 

	 $("#destination").geocomplete({}); 

 var longval1 = "#hidLong1";
 var latval1 = "#hidLat1";
 var map1;
 var marker1;
 var geocoder = new google.maps.Geocoder();    

 function initialize1() {
     //MAP
     var initialLat = $(latval1).val();
     var initialLong = $(longval1 ).val();
     var latlng = new google.maps.LatLng(initialLat, initialLong);
     var options = {
         zoom: 9,
         center: latlng,
         mapTypeId: google.maps.MapTypeId.ROADMAP
     };
 
     map1 = new google.maps.Map(document.getElementById("geomap1"), options);
     marker1 = new google.maps.Marker({
         map: map1,
         draggable: true,
         position: latlng
     });
     var marker2 = new google.maps.Marker({
         position: {lat: ${school_details.school_lat} , lng: ${school_details.school_lng} },
         map: map1,
         icon:'resources/dashboard/Images/school_icon.png'
       });
 
     google.maps.event.addListener(marker1, "dragend", function (event) {
         var point = marker1.getPosition();
         map1.panTo(point);
     });
     
 };
 
 $(document).ready(function () {
      initialize1();
 
     $(function () {
         $("#destination").autocomplete({
             //This bit uses the geocoder to fetch address values
             source: function (request, response) {
                 geocoder.geocode({ 'address': request.term }, function (results, status) {
                     response($.map1(results, function (item) {
                         return {
                             label: item.formatted_address,
                             value: item.formatted_address
                         };
                     }));
                 });
             }
         });
     });
 
    
     
     $('#destination').on("change",function (e) {
     	setTimeout(function(){ 
     	 var address =$("#destination").val();
         geocoder.geocode({ 'address': address }, function (results, status) {
             if (status == google.maps.GeocoderStatus.OK) {
                 map1.setCenter(results[0].geometry.location);
                 marker1.setPosition(results[0].geometry.location);
                 $(latval1).val(marker1.getPosition().lat());
                 $(longval1).val(marker1.getPosition().lng());
             } else {
                 alert("Geocode was not successful for the following reason: " + status);
             }
         });
         e.preventDefault();
     	 }, 400);
     });
 
     //Add listener to marker for reverse geocoding
     google.maps.event.addListener(marker1, 'drag', function () {
         geocoder.geocode({ 'latLng': marker1.getPosition() }, function (results, status) {
             if (status == google.maps.GeocoderStatus.OK) {
                 if (results[0]) {
                     $("#destination").val(results[0].formatted_address);
                     $(latval1).val(marker1.getPosition().lat());
                     $(longval1).val(marker1.getPosition().lng());
                 }
             }
         });
     });
 
 });
  
    </script>	
	
	
	
        

<script type="text/javascript">

function myfunction()
{
	 var longval = "#hidLong";
     var latval = "#hidLat";
	  if (document.getElementById('school_address').checked)  {
		 $("#source").val($("#school_add").val());
		 setTimeout(function(){ 
         	  map.setCenter(schoolLocation);
              marker.setPosition(schoolLocation);
         	  $(latval).val(marker.getPosition().lat());
              $(longval).val(marker.getPosition().lng());
            
             e.preventDefault();
         	 }, 400);
	  } else {
		  $("#source").val('');
		  $(latval).val('');
          $(longval).val("");
	  }
}

function myfunction1()
{
	 var longval = "#hidLong1";
     var latval = "#hidLat1";
	  if (document.getElementById('school_address1').checked)  {
		 $("#destination").val($("#school_add").val());
		 setTimeout(function(){ 
         	  map1.setCenter(schoolLocation);
              marker1.setPosition(schoolLocation);
         	  $(latval).val(marker.getPosition().lat());
              $(longval).val(marker.getPosition().lng());
            
             e.preventDefault();
         	 }, 400);
	  } else {
		  $("#destination").val('');
		  $(latval).val('');
          $(longval).val("");
	  }
}
</script>

<script type="text/javascript">


</script>      
<script src="resources/dashboard/dist/sweetalert-dev.js"></script>
<link rel="stylesheet" href="resources/dashboard/dist/sweetalert.css">
  <c:if test="${!empty success}">
<script>
swal(
		{
			title : "Route successfully added!",
			text : "",
			type : "success"
		} ) 
</script>

</c:if>    
<jsp:include page="footer.jsp" />      