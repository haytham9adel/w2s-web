<jsp:include page="header.jsp" />
  <%@page pageEncoding="UTF-8"%>
    <div class="section top_bread clearfix">
    	<div class="container">
			<div class="row">
				<div class="col-md-12">
                	<p class="align-right"><span>الصفحة الرئيسية </span>/<span  class="yellow">عنا</span></p>
                </div>
    		</div>
        </div>
    </div>
	<div class="section page_section clearfix">
    	<div class="container bg-body">
			<div class="row  bottom-margin ">
				<div class="col-md-12">
                	<h2>${content.heading5}</h2>
                </div>
    		</div>
        </div>
    </div>
    <div class="container  bg-body">
			<div class="row">
				<div class="col-md-12">
                	<div class="col-md-12 pad0" style="overflow:hidden;">
                    <h4 class="page-heading">${content.heading1}</h4>
                    <p>${content.description1}</p>
					<h4  class="page-heading">${content.heading2}</h4>
                   ${content.description2}
                    <h4 class="page-heading">${content.heading3}</h4>
                    ${content.description3}
                      <h4 class="page-heading">${content.heading4}</h4>
                      ${content.description4}
                    </div>
                    
                </div>
    		</div>
        </div>
 <jsp:include page="footer.jsp" />