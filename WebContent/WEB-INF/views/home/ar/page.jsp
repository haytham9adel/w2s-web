<jsp:include page="header.jsp" />
<%@page pageEncoding="UTF-8"%>
    <style>
       
    </style>
    
    <div class="section top_bread clearfix" >
    	<div class="container">
			<div class="row">
				<div class="col-md-12">
                	<p class="align-right"><span class="">الرئيسية</span>/<span  class="yellow">${content.page_name_ar}</span></p>
                </div>
    		</div>
        </div>
    </div>
	<div class="section clearfix">
    	<div class="container bg-body" >
			<div class="row">
				<div class="col-md-12">
                	<h2>${content.page_name_ar}</h2>
                </div>
    		</div>
        </div>
    </div>
    <div class="container  bg-body   bottom-margin ">
			<div class="row">
				<div class="col-md-12">
                	<div class="col-md-12 pad0">
                     
                    <p>${content.page_desc_ar}</p>
				    </div>
                    
                </div>
    		</div>
        </div>
 <jsp:include page="footer.jsp" />