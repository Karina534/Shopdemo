<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="org.example.shopdemo.utils.UrlPath" %>
<html>
<head>
    <title>Books Catalog</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" rel="stylesheet">
    <style>
        <%@include file='../../static/css/books.css' %>
    </style>
</head>
<body>
<div>
    <%@ include file="header.jsp"%>
</div>
    <h1>Каталог книг</h1>
    <div class="line">
        <c:forEach var="book" items="${requestScope.books}">
            <a href="${pageContext.request.contextPath}${UrlPath.BOOK_PAGE}?bookId=${book.booksId}">
                <div>
                    <img src="${pageContext.request.contextPath}${book.imgUrl}" alt="Book Image"><br>

                    <span>${book.title}</span>
                        <span>${book.author}</span>
                        <span>${book.price} ${book.getCurrencyName()}</span>

                    <form action="${pageContext.request.contextPath}${UrlPath.ADD_BOOK_IN_BASKET}?bookId=${book.booksId}" method="post" class="icon-container">
                        <button type="submit"><i class="fa fa-shopping-basket"></i></button>
                    </form>
                </div>
            </a>
        </c:forEach>
    </div>
</body>
</html>
