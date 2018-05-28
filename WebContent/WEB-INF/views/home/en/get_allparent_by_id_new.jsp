 <style>
 	.parent_hidden {
    border: 1px solid #ccc;
    padding: 1px;
    overflow: hidden;
}
.parent_hidden p {
    padding: 7px;
    float: left;
    background-color: #efefef;
    margin: 4px;
    border-radius: 10px;
        cursor: no-drop;
}
 </style>
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<% int i=1; %>
               
	<c:forEach items="${allParent}" var="parent">
	
		<div class="info-block">
			<h4>
				<b>Parent - <%=i %></b>
			</h4>
			<div class="info-group">
				<div class="col-a">Name :</div>
				<div class="col-b">${parent.first_name} ${parent.middle_name} ${parent.family_name} </div>
			</div>
		 <div class="info-group">
				<div class="col-a">Email :</div>
				<div class="col-b">${parent.user_email}</div>
		 </div>
		 <div class="info-group">
				<div class="col-a">Contact No :</div>
				<div class="col-b">${parent.contact_number}</div>
		 </div>
				
		</div>
		<% i++; %>	
 </c:forEach> 
   