<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>와이파이 정보 구하기</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body class="result-page">
<div class="container">
    <h1><%= request.getAttribute("message") %></h1>
    <a href="${pageContext.request.contextPath}/public_wifi.jsp">홈 으로 가기</a>
</div>
</body>
</html>
