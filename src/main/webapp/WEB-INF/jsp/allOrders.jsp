<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.example.shopdemo.utils.UrlPath" %>
<%@ page import="org.example.shopdemo.entity.Statuses" %>
<html>
<head>
    <title>All orders</title>
    <style>
        <%@include file='../../static/css/allOrders.css' %>
    </style>
</head>
<body>
<div>
    <%@ include file="header.jsp"%>
</div>

<h1>Все заказы</h1>

<div class="orderInfo">
    <c:forEach var="order" items="${orders}">
        <p class="orderT">Заказ ${order.orderId}</p>
        <span>Стоимость: ${order.totalPrice}</span><br>
        <span>Дата заказа: ${order.creationDate}</span><br>
        <span>Планируемая дата выполнения заказа: ${order.endDate}</span><br>
        <span>Статус заказа: ${Statuses.fromId(order.orderId).getDescription()}</span><br>
        <span>Адрес доставки: ${order.address}</span><br>
        <a href="${pageContext.request.contextPath}${UrlPath.ORDER_DETAILS}?orderId=${order.orderId}">Посмотреть детали заказа</a>
    </c:forEach>
</div>

</body>
</html>
