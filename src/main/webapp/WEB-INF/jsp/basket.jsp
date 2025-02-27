<%--
  Created by IntelliJ IDEA.
  User: smirn
  Date: 03.02.2025
  Time: 20:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Basket</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" rel="stylesheet">
    <script src="https://kit.fontawesome.com/d6d21c262f.js" crossorigin="anonymous"></script>
    <style>
        <%@include file='../../static/css/books.css' %>
    </style>
</head>
<body>
<div>
    <%@ include file="header.jsp"%>
</div>
<h1>Корзина</h1>
<div class="line">
    <c:forEach var="book" items="${requestScope.books}">
        <a href="${pageContext.request.contextPath}${UrlPath.BOOK_PAGE}?bookId=${book.booksId}">
            <div>
                <img src="${pageContext.request.contextPath}${book.imgUrl}" alt="Book Image"><br>

                <span>${book.title}</span>
                <span>${book.author}</span>
                <span>${book.price} ${book.getCurrencyName()}</span>
                <span>Количество: ${book.quantity}</span>

                <form action="${pageContext.request.contextPath}${UrlPath.DELETE_BOOK_FROM_BASKET}?basketId=${basketId}&bookId=${book.booksId}&quantity=${book.quantity}" method="post" class="icon-container">
                    <button type="submit"><i class="fa-solid fa-trash"></i></button>
                </form>
            </div>
        </a>
    </c:forEach>
</div>

<div class="buyBooks">
    <a href="${pageContext.request.contextPath}${UrlPath.BUY_BOOKS}?basketId=${basketId}">Купить</a>
</div>
</body>
</html>
