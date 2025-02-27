<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.example.shopdemo.utils.UrlPath" %>
<html>
<head>
    <title>Order Books Page</title>
    <style>
        <%@include file='../../static/css/orderBooksPage.css' %>
    </style>
</head>
<body>
<div>
    <%@ include file="header.jsp"%>
</div>

<h1>Детали заказа: ${orerB.orderId}</h1>
<div class="orderInfo">
    <c:forEach var="orerB" items="${orderBooks}">
        <div class="bookText">
            <a href="${pageContext.request.contextPath}${UrlPath.BOOK_PAGE}?bookId=${orerB.book.bookId}">
                <span class="bookInfo">${orerB.book.title}</span><br>
                <span class="bookInfo">${orerB.book.author}</span><br>
                <span class="bookInfo">Количество экземпляров: ${orerB.quantity}</span>
            </a>
        </div>
    </c:forEach>
</div>
</body>
</html>
