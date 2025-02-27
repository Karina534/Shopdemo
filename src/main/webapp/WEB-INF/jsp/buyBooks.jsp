<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.example.shopdemo.utils.UrlPath" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Buy Books</title>
    <style>
        <%@include file='../../static/css/buyBooks.css' %>
    </style>
</head>
<div>
    <%@ include file="header.jsp"%>
</div>
<body>
    <h1>Оформить заказ</h1>
    <div class="fullBlock">
    <div class="orderInfo">
        <c:forEach var="basketBook" items="${basketBooks}">
            <a href="${pageContext.request.contextPath}${UrlPath.BOOK_PAGE}?bookId=${basketBook.book.bookId}">
                <span>${basketBook.book.title}</span>
                <span>${basketBook.book.author}</span><br>
                <span>${basketBook.book.price}  ${basketBook.book.currency.getDescription()}</span><br>
                <span>Количество: ${basketBook.quantity}</span><br>
            </a><br><br>
        </c:forEach>
    </div>

    <p>Сумма заказа: ${fullCost} ${basketBooks[0].book.currency.getDescription()}</p>

    <form action="${pageContext.request.contextPath}${UrlPath.BUY_BOOKS}?fullCost=${fullCost}&consumerId=${basketBooks[0].basket.consumer.consumerId}&basketId=${basketBooks[0].basket.basketId}" method="post">
        <label for="address">Введите адрес доставки</label>
        <input type="text" id="address" name="address" required><br>

        <button>Оформить заказ</button>
    </form>
    </div>
</body>
</html>
