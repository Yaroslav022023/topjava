<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="css/style.css">
    <jsp:useBean id="mealsTo" class="java.util.ArrayList" scope="request">
        <jsp:setProperty name="mealsTo" property="*"/>
    </jsp:useBean>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<table class="table-bordered">
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
    </tr>
    <c:forEach var="mealTo" items="${mealsTo}">
        <jsp:useBean id="mealTo" type="ru.javawebinar.topjava.model.MealTo"/>
        <c:choose>
            <c:when test="${mealTo.excess}">
                <tr style="color: red;">
                    <td>${mealTo.dateTimeFormatted}</td>
                    <td>${mealTo.description}</td>
                    <td>${mealTo.calories}</td>
                </tr>
            </c:when>
            <c:otherwise>
                <tr style="color: green;">
                    <td>${mealTo.dateTimeFormatted}</td>
                    <td>${mealTo.description}</td>
                    <td>${mealTo.calories}</td>
                </tr>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</table>
</body>
</html>