<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Order Details</title>
    <style>
        <%@include file='../../static/css/orderDetails.css' %>
    </style>
</head>
<body>
<div>
    <%@ include file="header.jsp"%>
</div>

<h1>Детали заказа: ${orderDto.orderId}</h1>
<div>
    <span>Стоимость: ${orderDto.totalPrice}</span><br>
    <span>Дата создания: ${orderDto.creationDate}</span><br>
    <span>Дата планируемого окончания: ${orderDto.endDate}</span><br>
    <span>Статус заказа: ${orderDto.getStatus().getDescription()}</span><br>
    <span>Адрес доставки: ${orderDto.address}</span><br>
</div>
<br>
<span>Установите статус заказа:</span>
<form action="${pageContext.request.contextPath}${UrlPath.ORDER_DETAILS}?orderId=${orderDto.orderId}" method="post">
    <label for="status"></label>
    <select name="statusId" id="status">
        <c:forEach var="status" items="${statuses}">
            <option value="${status.getId()}">${status.getDescription()}</option>
        </c:forEach>
    </select><br><br>
    <button type="submit">Установить</button>
</form>
<c:if test="${ not empty requestScope.errors}">
    <div style="color: red">
        <c:forEach var="error" items="${requestScope.errors}">
            <span>${error.message}</span>
            <br>
        </c:forEach>
    </div>
</c:if>
</body>
</html>
