
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.example.shopdemo.utils.UrlPath" %>
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" rel="stylesheet">
<style>
    <%@include file='../../static/css/header.css' %>
</style>
<div class="header">
    <a class="headerText" href="${pageContext.request.contextPath}${UrlPath.BOOKS}">Каталог</a>

    <c:if test="${not empty sessionScope.consumer}">
        <a class="headerText" href="${pageContext.request.contextPath}${UrlPath.BASKET}">Корзина</a>
    </c:if>

    <a class="headerText" href="${pageContext.request.contextPath}${UrlPath.PERSONAL_ACCOUNT}">Личный кабинет</a>

    <span>
        <c:if test="${not empty sessionScope.consumer or not empty sessionScope.admin}">
            <form action="${pageContext.request.contextPath}${UrlPath.LOGOUT}" method="post">
                <button class="headerText" type="submit">Logout</button>
            </form>
        </c:if>
        <c:if test="${empty sessionScope.consumer and empty sessionScope.admin}">
            <a class="headerText" href="${pageContext.request.contextPath}${UrlPath.LOGIN}">Войти</a>
            <span class="headerText">/</span>
            <a class="headerText" href="${pageContext.request.contextPath}${UrlPath.REGISTRATION}">Регистрация</a>
        </c:if>
    </span>
</div>