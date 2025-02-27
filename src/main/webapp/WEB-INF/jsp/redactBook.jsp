<%--
  Created by IntelliJ IDEA.
  User: smirn
  Date: 31.01.2025
  Time: 21:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>redactBook</title>
</head>
<body>
<%@ include file="header.jsp"%>
<div>
    <h2>Отредактируйте книгу:</h2>
    <form action="${pageContext.request.contextPath}${UrlPath.BOOK_REDUCTION}" method="post">
        <input type="hidden" name="bookId" value="${books.bookId}">

        <label for="author">Автор: </label>
        <input type="text" name="author" id="author" value="${books.author}" required><br><br>

        <label for="title">Название: </label>
        <input type="text" name="title" id="title" value="${books.title}" required><br><br>

        <label for="description">Описание: </label>
        <textarea type="text" name="description" id="description">${books.description}</textarea><br><br>

        <label for="price">Цена: </label>
        <input type="number" name="price" id="price" step="0.01" value="${books.price}" required><br><br>

        <label for="currency"></label>
        <select name="currencyId" id="currency">
            <c:forEach var="currency" items="${currencies}">
                <option value="${currency.getId()}" ${currency.getId() == book.currencyId ? 'selected' : ''}>
                ${currency.getDescription()}
                </option>
            </c:forEach>
        </select><br><br>

        <label for="remains">Количество: </label>
        <input type="number" name="remains" id="remains" value="${books.remains}" required><br><br>

        <label for="publisher"></label>
        <select name="publisherId" id="publisher">
            <c:forEach var="publisher" items="${publishers}">
                <option value="${publisher.getPublisherId()}" ${publisher.getPublisherId() == book.publisherId ? 'selected' : ''}>
                        ${publisher.getName()}
                </option>
            </c:forEach>
        </select><br><br>

<%--        <label for="image">Выберете изображения: </label>--%>
<%--        <input type="file" name="image" id="image" multiple><br><br>--%>

        <button type="submit">Обновить</button>
    </form>
</div>
</body>
</html>
