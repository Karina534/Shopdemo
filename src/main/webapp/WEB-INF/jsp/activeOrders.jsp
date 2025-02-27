<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.example.shopdemo.utils.UrlPath" %>
<html>
<head>
    <title>Active Orders</title>
    <style>
        <%@include file='../../static/css/activeOrders.css' %>
    </style>
</head>
<body>
<div>
    <%@ include file="header.jsp"%>
</div>

<h1>Текущие заказы</h1>
<div class="orderInfo">
    <c:forEach var="order" items="${orders}">
        <p>Заказ: ${order.orderId}</p>
        <span>Сумма заказа: ${order.totalPrice}</span><br>
        <span>Дата заказа: ${order.creationDate}</span><br>
        <span>Планируемая дата завершения заказа: ${order.endDate}</span><br>
        <span>Статус заказа: ${order.status.getDescription()}</span><br>
        <span>Адрес доставки: ${order.address}</span><br>

        <a href="${pageContext.request.contextPath}${UrlPath.ORDER_BOOKS_PAGE}?orderId=${order.orderId}">Посмотреть детали заказа</a>
    </c:forEach>
</div>
</body>
</html>
