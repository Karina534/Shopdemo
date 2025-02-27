<%--
  Created by IntelliJ IDEA.
  User: smirn
  Date: 12.01.2025
  Time: 15:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="org.example.shopdemo.utils.UrlPath" %>
<html>
<head>
    <title>Enter</title>
</head>
<body>
<div>
    <%@ include file="header.jsp"%>
</div>
    <h1>Вход</h1>
    <form action="${pageContext.request.contextPath}${UrlPath.LOGIN}" method="post">
        <label for="email">Электронная почта: </label>
        <input type="text" name="email" id="email" required><br><br>

        <label for="password">Пароль: </label>
        <input type="password" name="password" id="password" required><br><br>

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

        <button type="submit">Войти</button>
    </form>

    <c:if test="${ not empty requestScope.errors}">
        <div style="color: red">
            <c:forEach var="error" items="${requestScope.errors}">
                <span>${error.message}</span>
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