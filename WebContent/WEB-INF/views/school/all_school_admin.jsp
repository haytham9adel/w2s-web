<jsp:include page="header.jsp" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="java.util.*" %> 
<%@ page import="org.apache.commons.codec.binary.Base64" %> 
<script src="resources/dashboard/dist/sweetalert-dev.js"></script>
<link rel="stylesheet" href="resources/dashboard/dist/sweetalert.css">
<link href="https://cdn.datatables.net/1.10.13/css/dataTables.material.min.css" rel="stylesheet" type="text/css">
<link href="https://cdnjs.cloudflare.com/ajax/libs/material-design-lite/1.1.0/material.min.css" rel="stylesheet" type="text/css">
<script type="text/javascript" language="javascript" src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js">
</script>
<script type="text/javascript" language="javascript" src="https://cdn.datatables.net/1.10.13/js/dataTables.material.min.js">
</script>

 <script>
 $(document).ready(function() {
	    $('#example').DataTable({
	    	"pagingType": "simple_numbers",
	    	"oLanguage": {
	    		"sUrl": "resources/dashboard/js/english.json"
	    	},
	    	 aoColumnDefs : [{
	    	      orderable : false, aTargets : [5] //disable sorting for the 1st column
	    	 		  
	    	 }],
	    	 columnDefs: [
	    	              {
	    	                  targets: [ 0, 1, 2 ],
	    	                  className: 'mdl-data-table__cell--non-numeric'
	    	              }
	    	          ],
	    });
	} );
 </script>

<div class="col-md-9 pad0">
	<div class="col-sm-12 pad0">
	<% if((Integer)session.getAttribute("permission")==1) {%>
	<a href="school/addSchoolAdmin/" class="btn btn-primary btn-submit" style="float:right; margin:12px ">Add New</a>
	<% } %>
	    <span class="success">${success}</span>
             <span class="error">${error}</span>
	<table id="example" class="mdl-data-table" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Username</th>
               <!--  <th>Privileges</th> -->
                <th>Email</th>
                <th>Mobile Number</th>
                <th>Action</th>
            </tr>
        </thead>
 
         <tbody>
         <c:set var="count" value="1" scope="page" />



                <c:forEach items="${parents}" var="parent">
    	<tr>
    	<td><c:out value="${count}"/></td>
    	<td>${parent.first_name} ${parent.last_name} </td>
    	<td>${parent.user_name}</td>
    	<%-- <td>
    	<c:choose>
    	<c:when test="${parent.permission==0}">
									Just View
									</c:when>
									<c:otherwise>
									Main School Admin
									</c:otherwise>
	                            </c:choose></td> --%>
    	<td><c:out value="${parent.user_email}"/></td>
    	<td><c:out value="${parent.contact_number}"/></td>
    	<td style="min-width:93px !important;">
		<c:set var="parent_id" value="${parent.user_id}"/>
		<%
		String str=pageContext.getAttribute("parent_id").toString();
		byte[] encodedBytes = Base64.encodeBase64(str.getBytes());
		%>
    	<%-- <a title="View" href="school/viewSchoolAdmin?q=<%=new String(encodedBytes)%>"><i class="fa fa-eye fa-2x edit"></i>	</a>
    	<% if((Integer)session.getAttribute("permission")==1) {%>
    	<a title="Edit" href="school/editSchoolAdmin?q=<%=new String(encodedBytes)%>"><i class="fa fa-pencil-square-o fa-2x edit"></i></a>
    	
    	
    	<a title="Delete"
onClick="javascript:return confirm('Are you sure to delete it?')" href="school/deleteSchoolAdmin?user_id=${parent.user_id}"><i class="fa fa-times fa-2x delete"></i></a>
    	<% } %> --%>
    	
    	<div class="btn-group">
	          <a title="View" class="btn btn-danger"  href="school/viewSchoolAdmin?q=<%=new String(encodedBytes)%>"><i class="fa fa-eye edit"></i>	</a>
		    	<% if((Integer)session.getAttribute("permission")==1) {%>
		    	<a title="Edit" class="btn btn-danger"  href="school/editSchoolAdmin?q=<%=new String(encodedBytes)%>"><i class="fa fa-pencil-square-o edit"></i></a>
		    	<a title="Delete"  class="btn btn-danger" 
		onClick="javascript:return confirm('Are you sure to delete it?')" href="javascript:void(0);"><i class="fa fa-trash-o delete"></i></a>
		<% } %>
			          
	        </div>
    	</td>
    	</tr>     
    <c:set var="count" value="${count + 1}" scope="page"/>
     	 </c:forEach>
      
          </tbody>  
        
       </table> 	
	
	</div>
            
</div>
<script>
/* function confirm()
{
	swal({   title: "Are you sure?",   text: "to delete School Admin",   type: "warning",   showCancelButton: true,   confirmButtonColor: "#DD6B55",   confirmButtonText: "Yes, delete it!",   closeOnConfirm: true }, function(){    });
}
 */

</script>
          
<jsp:include page="footer.jsp" />      