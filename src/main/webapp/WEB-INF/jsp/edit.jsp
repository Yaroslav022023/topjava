<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="css/style.css">
    <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
    <title>Meal</title>
</head>
<body>
<jsp:include page="fragments/header.jsp"/>
<div class="form-container">
    <h2>Edit Meal</h2>
    <form action="meals" method="post">
        <input type="hidden" id="id" name="id" value="${id}">
        <div class="form-input">
            <label for="dateTime">DateTime:</label>
            <input type="datetime-local" id="dateTime" name="dateTime" value="2020-01-30T10:00">
        </div>
        <div class="form-input">
            <label for="description">Description:</label>
            <input type="text" id="description" name="description" value="Завтрак">
        </div>
        <div class="form-input">
            <label for="calories">Calories:</label>
            <input type="number" id="calories" name="calories" value="500">
        </div>
        <div class="form-input">
            <input type="submit" value="Save">
            <input type="button" value="Cancel" onclick="history.back();">
        </div>
    </form>
</div>
</body>
</html>