<%--
  Created by IntelliJ IDEA.
  User: smirn
  Date: 11.01.2025
  Time: 19:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="org.example.shopdemo.utils.UrlPath" %>

<html>
<head>
    <title>Registration</title>
    <style>
        <%@include file='../../static/css/registration.css' %>
    </style>
</head>
<body>
<div>
    <%@ include file="header.jsp"%>
</div>
    <p>Регистрация</p>
        <form action="${pageContext.request.contextPath}${UrlPath.REGISTRATION}" method="post">
            <label for="consumerName">Имя: </label>
            <input type="text" name="consumerName" id="consumerName" required><br><br>

            <label for="surname">Фамилия: </label>
            <input type="text" name="surname" id="surname" required><br><br>

            <label for="email">Электронная почта: </label>
            <input type="text" name="email" id="email" required><br><br>

            <label for="password">Пароль: </label>
            <input type="password" name="password" id="password" required><br><br>

            <label for="telephone">Телефон: </label>
            <input type="text" name="telephone" id="telephone" required><br><br>

            <label for="role">Роль: </label>
            <select name="role" id="role" required onchange="toggleIndividualNumField()">
                <c:forEach var="role" items="${requestScope.roles}">
                    <option label="${role.getDescription()}">${role}</option>
                </c:forEach>
            </select><br><br>

            <div id="individualNumField" style="display: none;">
                <label for="individualNum">Индивидуальный номер: </label>
                <input type="password" name="individualNum" id="individualNum"><br><br>
            </div>

            <button type="submit">Зарегистрироваться</button>
        </form>

        <c:if test="${ not empty requestScope.errors}">
            <div style="color: red">
                <c:forEach var="errror" items="${requestScope.errors}">
                    <span>${errror.message}</span>
                    <br>
                </c:forEach>
            </div>
        </c:if>
</body>
</html>

<script>
    function toggleIndividualNumField() {
        const roleSelect = document.getElementById('role');
        const individualNumField = document.getElementById('individualNumField');
        const individualNumInput = document.getElementById('individualNum');

        if (roleSelect.value === 'A') {
            individualNumField.style.display = 'block';
            individualNumInput.setAttribute('required', 'required');
        } else {
            individualNumField.style.display = 'none';
            individualNumInput.removeAttribute('required');
        }
    }

    // Чтобы убедиться, что поле корректно отображается при загрузке страницы
    window.onload = toggleIndividualNumField;
</script>
