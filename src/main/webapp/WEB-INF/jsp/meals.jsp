<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="css/style.css">
    <jsp:useBean id="mealsToList" class="java.util.ArrayList" scope="request">
        <jsp:setProperty name="mealsToList" property="*"/>
    </jsp:useBean>
    <title>Meals</title>
</head>
<body>
<jsp:include page="fragments/header.jsp"/>
<h2>Meals</h2>
<h3><a href="meals?action=create">Add Meal</a></h3>
<table class="table-bordered">
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <th></th>
        <th></th>
    </tr>
    <c:forEach var="mealTo" items="${mealsToList}">
        <jsp:useBean id="mealTo" type="ru.javawebinar.topjava.model.MealTo"/>
        <tr style="color: ${mealTo.excess ? 'red' : 'green'};">
            <td>${TimeUtil.getDateTimeFormatted(mealTo.dateTime)}</td>
            <td>${mealTo.description}</td>
            <td>${mealTo.calories}</td>
            <td><a href="meals?id=${mealTo.id}&action=update">Update</a></td>
            <td><a href="meals?id=${mealTo.id}&action=delete">Delete</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>