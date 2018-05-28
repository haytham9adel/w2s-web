<jsp:include page="header.jsp" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!--  Login Section -->
<%@page pageEncoding="UTF-8"%>
<section class="login-page">

	<div class="col-sm-4 loginbox">
		<center>الدخول الى نظام معاك </center>
		
	
		<c:if test="${!empty error}">
		<div class="alert alert-danger error" style="font-size: 16px;">
			<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
		  ${error}
		</div>  
		</c:if>
		<form action="login.html" method="post" id="login-form">
			<div class="form-group">
				<label>اسم المستخدم</label>
				<input type="text" name="user_name" class="form-control" id="user_name">
			</div>
			
			<div class="form-group">
				<label>كلمة المرور</label>
				<input type="password" name="user_pass" class="form-control" id="user_pass">
			</div>
			
			<div class="form-group">
				<button type="submit" class="btn btn-primary btn-submit">إرسال</button>
			</div>
			<div class="clearfix"></div>
		</form>
	</div>
</section>
<!-- End login section -->
<script>
$(document).ready(function(){
	$('header').addClass('fix_header');
});
</script>

        
   <script src="resources/front/js/jquery.validate.min.js"> </script>
  <script>
  $(document).ready(function(){
		$("#login-form").validate({
          rules: {
		          user_pass: "required",
		          user_name:{
						required: true,
						
					}
		     
          },
          messages: {
        	  user_pass: "Please enter password",
        	  user_name: {
				required:"Please enter username",
				
			}
	      },
          submitHandler: function(form) {
              form.submit();
          }
      });
      

});
  
  </script>         