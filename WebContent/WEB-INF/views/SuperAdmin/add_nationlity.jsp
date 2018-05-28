<jsp:include page="header.jsp" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
 
<div class="col-md-6 col-sm-6 col-xs-12 m-side">
        <div class="new-student-form"> 
              <span class="success">${success}</span>
             <span class="error">${error}</span>
                <form class="form-horizontal" id="holiday" method="post">
              
                    <div class="form-group">
                     <label class="col-sm-3 control-label">Nationlity Name :</label>
                       <div class="col-sm-9">
                         <input type="text" name="name" value="${nation.name}" id="name" class="form-control">
                        </div>
                   </div>
                    
                   
                 <div class="form-group">
                       <div class="col-sm-9">
                        <input  class="btn btn-primary btn-submit" value="Save" type="submit">&nbsp;&nbsp;
                        <a href="admin/manageNationlities">
                        <input  class="btn btn-primary btn-submit" value="Cancel" type="button">
                        </a>
                       </div>
                   </div>
                </form>
             </div> 
             
         <style>
section.tb-chat {
	padding: 40px 0;
}
.conversation-wrap {
	box-shadow: -2px 0 3px #ddd;
	padding: 0;
	max-height: 400px;
	overflow: auto;
	background: #f1f1f1;
}
.conversation {
	padding: 5px;
	border-bottom: 1px solid #ddd;
	margin: 0;
}
.message-wrap {
	box-shadow: 0 0 3px #ddd;
	padding: 0;
}
.msg {
	padding: 5px;
	/*border-bottom:1px solid #ddd;*/
	margin: 0;
}
.msg-wrap {
	padding: 10px;
	max-height: 400px;
	overflow: auto;
}
.time {
	color: #bfbfbf;
}
.send-wrap {
	border-top: 1px solid #eee;
	border-bottom: 1px solid #eee;
	padding: 10px;/*background: #f8f8f8;*/
}
.send-message {
	resize: none;
}
.highlight {
	background-color: #f7f7f9;
	border: 1px solid #e1e1e8;
}
.send-message-btn {
	border-top-left-radius: 0;
	border-top-right-radius: 0;
	border-bottom-left-radius: 0;
	border-bottom-right-radius: 0;
}
.btn-panel {
	background: #f7f7f9;
}
.btn-panel .btn {
	color: #b8b8b8;
	transition: 0.2s all ease-in-out;
}
.btn-panel .btn:hover {
	color: #666;
	background: #f8f8f8;
}
.btn-panel .btn:active {
	background: #f8f8f8;
	box-shadow: 0 0 1px #ddd;
}
.btn-panel-conversation .btn, .btn-panel-msg .btn {
	background: #f8f8f8;
}
.btn-panel-conversation .btn:first-child {
	border-right: 1px solid #ddd;
}
.msg-wrap .media-heading {
	color: #003bb3;
	font-weight: 700;
}
.msg-date {
	background: none;
	text-align: center;
	color: #aaa;
	border: none;
	box-shadow: none;
	border-bottom: 1px solid #ddd;
}
 body::-webkit-scrollbar {
 width: 12px;
}
 
    
    /* Let's get this party started */
    ::-webkit-scrollbar {
 width: 6px;
}

    /* Track */
    ::-webkit-scrollbar-track {
 -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
/*        -webkit-border-radius: 10px;
        border-radius: 10px;*/
}

    /* Handle */
    ::-webkit-scrollbar-thumb {
/*        -webkit-border-radius: 10px;
        border-radius: 10px;*/
        background:#ddd;
 -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.5);
}
    ::-webkit-scrollbar-thumb:window-inactive {
 background: #ddd;
}
</style>
   </div>
         
        <script src="resources/front/js/jquery.validate.min.js"> </script>
  <script>
  $(document).ready(function(){
		$("#holiday").validate({
			rules: {
	        	  name: "required",
	          },
	          messages: {
	        	 name:"Please enter nationlity name"
	         },
          submitHandler: function(form) {
              form.submit();
          }
      });
      

});
  
  </script>

          
    <jsp:include page="footer.jsp" />      