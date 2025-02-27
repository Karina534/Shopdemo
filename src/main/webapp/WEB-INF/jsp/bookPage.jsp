<%--
  Created by IntelliJ IDEA.
  User: smirn
  Date: 07.01.2025
  Time: 19:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="org.example.shopdemo.utils.UrlPath" %>

<html>
<head>
    <meta charset="utf-8">
    <title>Title</title>
    <style>
        <%@include file='../../static/css/bookPage.css' %>
    </style>
    <script type="text/javascript"><%@include file="bookPage.js" %></script>
</head>
<body>
<div>
    <%@ include file="header.jsp"%>
</div>
<div>
    <c:if test="test=${adminCookie != null}">
        <a href="${pageContext.request.contextPath}${UrlPath.BOOK_REDUCTION}?bookId=${bookPage.booksId}">Редактировать книгу</a>
    </c:if>
</div>
<div class="fullContext">
    <!-- Карусель изображений -->
    <div class="image-carousel">
        <div class="carousel-images">
            <c:forEach var="bookImage" items="${bookImagesUrls}">
                <img src="${pageContext.request.contextPath}${bookImage}" alt="Book Image">
            </c:forEach>
        </div>
        <button class="carousel-button prev" onclick="prevSlide()">&#9664;</button>
        <button class="carousel-button next" onclick="nextSlide()">&#9654;</button>
    </div>

    <div class="context">
        <h1>${bookPage.title}</h1>
        <h3>${bookPage.author}</h3>
        <p>${bookPage.price} ${bookPage.getCurrencyName()}</p>
        <p>Описание:</p>
        <p>${bookPage.description}</p>
        <p>Осталось экземпляров: ${bookPage.remains}</p>
        <p>Издатель: ${bookPage.getPublisherName()}</p>
        <br><br>

        <div>
            <c:if test="${not empty sessionScope.admin}">
                <a class="aminButtons" href="${pageContext.request.contextPath}${UrlPath.BOOK_REDUCTION}?bookId=${bookPage.booksId}">Редактировать книгу</a><br>
                <a class="aminButtons" href="${pageContext.request.contextPath}${UrlPath.DELETE_BOOK}?bookId=${bookPage.booksId}">Удалить книгу</a>
            </c:if>
        </div>
    </div>
</div>
</body>
</html>
