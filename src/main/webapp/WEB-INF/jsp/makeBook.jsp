<%--
  Created by IntelliJ IDEA.
  User: smirn
  Date: 09.01.2025
  Time: 14:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="org.example.shopdemo.utils.UrlPath" %>

<html>
<head>
    <title>Make book</title>
</head>
<body>
<div>
    <%@ include file="header.jsp"%>
</div>
    <h2>Добавьте книгу в каталог:</h2>
    <form action="${pageContext.request.contextPath}${UrlPath.MAKE_BOOK}" method="post" enctype="multipart/form-data">
        <label for="author">Автор: </label>
        <input type="text" name="author" id="author" required><br><br>

        <label for="title">Название: </label>
        <input type="text" name="title" id="title" required><br><br>

        <label for="description">Описание: </label>
        <textarea type="text" name="description" id="description"></textarea><br><br>

        <label for="price">Цена: </label>
        <input type="number" name="price" id="price" step="0.01" required><br><br>

        <label for="currency"></label>
        <select name="currencyId" id="currency">
            <c:forEach var="currency" items="${currencies}">
                <option value="${currency.getId()}">${currency.getDescription()}</option>
            </c:forEach>
        </select><br><br>

        <label for="remains">Количество: </label>
        <input type="number" name="remains" id="remains" required><br><br>

        <label for="publisher"></label>
        <select name="publisherId" id="publisher">
            <c:forEach var="publisher" items="${publishers}">
                <option value="${publisher.getPublisherId()}">${publisher.getName()}</option>
            </c:forEach>
        </select><br><br>

        <label for="image">Выберете изображения: </label>
        <input type="file" name="image" id="image" multiple><br><br>

        <button type="submit">Создать</button>
    </form>
</body>
</html>
