<%--
  Created by IntelliJ IDEA.
  User: smirn
  Date: 11.01.2025
  Time: 20:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="org.example.shopdemo.utils.UrlPath" %>
<html>
<head>
    <title>Personal Account</title>
    <style>
        <%@include file='../../static/css/personalAccount.css' %>
    </style>
</head>
<body>
<%@ include file="header.jsp"%>
    <h1>Персональный аккаунт</h1>

<div class="userInfo">
    <h2>Персональные данные</h2>
    <c:choose>
        <c:when test="${not empty requestScope.consumer}">
            <p>Имя пользователя: ${requestScope.consumer.consumerName}</p>
            <p>Фамилия пользователя: ${requestScope.consumer.surname}</p>
            <p>Электронная почта: ${requestScope.consumer.email}</p>
            <p>Номер телефона: ${requestScope.consumer.telephone}</p>
            <a href="${pageContext.request.contextPath}${UrlPath.ACTIVE_ORDERS}">Посмотреть заказы</a>
        </c:when>
        <c:otherwise>
            <div>
                <p>Имя администратора: ${requestScope.admin.name}</p>
                <p>Фамилия администратора: ${requestScope.admin.surname}</p>
                <p>Электронная почта: ${requestScope.admin.email}</p>
                <p>Номер телефона: ${requestScope.admin.telephone}</p>
                <p>Индивидуальный номер: ${requestScope.admin.individualNum}</p>
            </div>
            <br><br>
            <div>
                <a href="${pageContext.request.contextPath}${UrlPath.MAKE_BOOK}">Создать книгу</a><br>
                <a href="${pageContext.request.contextPath}${UrlPath.ALL_ORDERS}">Посмотреть заказы</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
