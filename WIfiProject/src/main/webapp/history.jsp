<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>와이파이 정보 구하기</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<h1>위치 히스토리 목록</h1>
<nav>
    <ul class="nav-menu">
        <li><button id="home-button">홈</button></li>
        <li><button id="history-button">위치 히스토리 목록</button></li>
        <li><button id="fetch-data-button">Open API 와이파이 정보 가져오기</button></li>
        <li><button id="bookmark-button">북마크 보기</button></li>
        <li><button id="bookmark-group-button">북마크 그룹 관리</button></li>
    </ul>
</nav>
<table class="table">
    <thead>
    <tr class="success">
        <th>ID</th>
        <th>X 좌표</th>
        <th>Y 좌표</th>
        <th>조회일자</th>
        <th>비고</th>
    </tr>
    </thead>
    <tbody id="history-results">
    </tbody>
</table>
<input type="hidden" id="context-path" value="${pageContext.request.contextPath}">
<script src="${pageContext.request.contextPath}/js/scripts.js" defer></script>
</body>
</html>
