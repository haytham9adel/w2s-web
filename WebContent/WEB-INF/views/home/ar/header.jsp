<% response.setHeader("Cache-Control", "no-cache"); %>
<% response.setHeader("Cache-Control", "must-revalidate"); %>
<% response.setHeader("Pragma","no-cache"); %>
<% response.setDateHeader("Last-Modified",System.currentTimeMillis());%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="resources.Assets" %>
<%@page language="java" contentType="text/html; charset=UTF-8"%>

<!DOCTYPE html>
<html lang="ar">
  <head>
  
  <%@page pageEncoding="UTF-8"%>
 <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
     
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <!-- The above 3 meta tags *must* come first in the head; any other head 
    content must come *after* these tags -->
    <title>M3AAk</title>
    <link rel="shortcut icon" href="resources/front/Images/favicon.png">
    <!-- Bootstrap -->
    	<link href="<c:url value="resources/front/css/bootstrap.min.css" />" rel="stylesheet">
    	<link href="<c:url value="resources/front/css/mystyle-ar.css" />" rel="stylesheet">
   		<link href="<c:url value="resources/front/css/responsive-ar.css" />" rel="stylesheet">
     <link href="resources/front/css/jquery.bxslider.css" rel="stylesheet" type="text/css">
     <link href="resources/front/css/owl.carousel.css" rel="stylesheet" type="text/css">
     
     <link href="resources/front/css/font-awesome.css" rel="stylesheet" type="text/css">
     <link href="resources/front/css/font-awesome.min.css" rel="stylesheet" type="text/css">
     
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
    <script src="resources/front/js/bootstrap.min.js"> </script>
    <script src="resources/front/js/jquery.bxslider.js"> </script>
    <script src="resources/front/js/owl.carousel.js"> </script>
    <link rel="shortcut icon" href="resources/front/Images/favicon.ico">
    
    <script src="resources/dashboard/js/jstz-1.0.4.min.js"> </script>
    <script language="javascript">
    function getTimezoneName() {
        timezone = jstz.determine()
       // return timezone.name();
        
        $.ajax({
            url : 'setTimeZoneInCookies.html',
            type:'post',
            data:{"name":timezone.name()},
            success : function(data) {
            }
        });
        
    }
    
</script>
    <script> 
	  $(document).ready(function(){
		  getTimezoneName();
  $('.bxslider').bxSlider({auto:true, pager: true});
});
    </script>
    
   <script>
    $(document).ready(function() {
 
  $("#owl-demo").owlCarousel({
 	
      autoPlay: 3000, //Set AutoPlay to 3 seconds
      items :3,
      dots: true,
      itemsDesktop : [1199,3],
      itemsDesktopSmall : [979,3]
 
  });
 
});
    </script>
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media 
     queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    <script type="text/javascript">
		$(document).ready(function($){
			var full_path = location.href.split("#")[0];
			$(".pav li a").each(function(){
			var $this = $(this);
			if($this.prop("href").split("#")[0] == full_path) {
				$(this).parents("li").addClass('active').siblings().removeClass('active');
				$(this).parents("ul").show();
						}
			});
		});
    </script>
    
  </head>
  <body>
  
  <a class="lan_switch" href="chnageLang.html?lang=en" id="lang_switcher">English</a>
 <div id="main-div" class="wrapper"> <!--========= wrapper-div===========--->
    <header> 
       <div class="head-top"> <!--===head-top ===--->
         <div class="container">
            <div class="user-panel pull-left">
             <a href="login.html"><button class="btn btn-default"> الدخول</button></a>
             <!--  <button class="btn btn-default">Sign up</button> -->
             </div> 
         </div>
       </div><!--===head-top ===--->
       <div class="head-bottom"><!--===head-bottom===--->
         <div class="container">
           <div class="row"> 
             <div class=" col-sm-3 col-xs-9 logo"> 
               <img src="resources/front/Images/logo.png">
             </div>
              <nav role="navigation" class="main-nav navbar-right">
                <div class="navbar-header">
                 <button data-target="#navbar-collapse" 
                  data-toggle="collapse" type="button" class="navbar-toggle">
                   <span class="sr-only">Toggle navigation</span>
                   <span class="icon-bar"></span>
                   <span class="icon-bar"></span>
                   <span class="icon-bar"></span>
                   </button><!--//nav-toggle-->
                   </div><!--//navbar-header-->
                   <div class="navbar-collapse collapse ar_menu" id="navbar-collapse">
                      <ul class="nav navbar-nav pav">
                       <li class="active nav-item"><a href="index.html">الرئيسية</a></li>
                       <li class="nav-item dropdown"><a class="dropdown-toggle" href="about.html">عنا</a>
                          
                       </li>
                       <li class="nav-item"><a href="features.html">الخصائص</a></li>
                       <li class="nav-item"><a href="how_it_works.html">كيف يعمل النظام</a></li>
                       <li class="nav-item"><a href="faq.html">أسئلة شائعة</a></li>
                       <li class="nav-item"><a href="contact.html">اتصل بنا</a>
                       </li>
                    </ul><!--//nav-->
                </div><!--//navabr-collapse-->
            </nav>
             
          </div>
          </div> 
       </div>
    </header>
    