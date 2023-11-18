<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<%--<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>--%>
<html>
<head>
    <title>Meal list</title>
    <style>
        .normal {
            color: green;
        }

        .excess {
            color: red;
        }

        #filter {
            max-width: 600px;
            margin: 20px 0;
            padding: 15px 15px 15px 0;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .form-control {
            display: block;
            width: 100%;
            padding: 8px 10px;
            font-size: 0.9rem;
            line-height: 1.3;
            color: #495057;
            background-color: #fff;
            background-clip: padding-box;
            border: 1px solid #ced4da;
            border-radius: 0.25rem;
            transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            font-size: 0.9rem;
        }

        .row {
            margin-right: -5px;
            margin-left: -5px;
        }

        [class*="col-"] {
            padding-right: 5px;
            padding-left: 5px;
        }

        @media (max-width: 576px) {
            .row {
                margin-right: 0;
                margin-left: 0;
            }

            [class*="col-"] {
                padding-right: 0;
                padding-left: 0;
            }

            #filter {
                max-width: 100%;
                margin: 10px 0;
                padding: 10px;
            }

            .form-control {
                padding: 6px 8px;
            }

            label {
                margin-bottom: 3px;
            }
        }
    </style>
</head>
<body>
<section>
    <h3><a href="index.html">Home</a></h3>
    <hr/>
    <h2>Meals</h2>
    <form id="filter" method="get">
        <div class="row">
            <div class="col-md-3">
                <label for="startDate">From date (including)</label>
                <input type="date" class="form-control" name="startDate" id="startDate" autocomplete="off">
            </div>
            <div class="col-md-3">
                <label for="endDate">Up to date (including)</label>
                <input type="date" class="form-control" name="endDate" id="endDate" autocomplete="off">
            </div>
            <div class="col-md-3">
                <label for="startTime">From time (including)</label>
                <input type="time" class="form-control" name="startTime" id="startTime" autocomplete="off">
            </div>
            <div class="col-md-3">
                <label for="endTime">Up to time (excluding)</label>
                <input type="time" class="form-control" name="endTime" id="endTime" autocomplete="off">
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 text-right">
                <button type="submit">Отфильтровать</button>
            </div>
        </div>
    </form>
    <div>
        <a href="meals?action=create">Add Meal</a>
        <br><br>
        <table border="1" cellpadding="8" cellspacing="0">
            <thead>
            <tr>
                <th>Date</th>
                <th>Description</th>
                <th>Calories</th>
                <th></th>
                <th></th>
            </tr>
            </thead>
            <c:forEach items="${requestScope.meals}" var="meal">
                <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
                <tr class="${meal.excess ? 'excess' : 'normal'}">
                    <td>
                            <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                            <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                            <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                            ${fn:formatDateTime(meal.dateTime)}
                    </td>
                    <td>${meal.description}</td>
                    <td>${meal.calories}</td>
                    <td><a href="meals?action=update&id=${meal.id}">Update</a></td>
                    <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>
                </tr>
            </c:forEach>
        </table>
    </div>
</section>
</body>
</html>