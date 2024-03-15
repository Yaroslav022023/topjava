<%@ page isErrorPage="true" contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<jsp:include page="fragments/headTag.jsp"/>

<body>
<jsp:include page="fragments/bodyHeader.jsp"/>
<div class="jumbotron">
    <div class="container text-center">
        <br>
        <h2 class="my-5">${typeMessage}</h2>
        <h4 class="my-5">${details}</h4>
    </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
</body>
</html>