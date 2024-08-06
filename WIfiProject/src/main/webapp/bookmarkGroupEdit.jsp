<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>북마크 그룹 수정</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<h1>북마크 그룹 수정</h1>
<nav>
    <ul class="nav-menu">
        <li><button id="home-button">홈</button></li>
        <li><button id="history-button">위치 히스토리 목록</button></li>
        <li><button id="fetch-data-button">Open API 와이파이 정보 가져오기</button></li>
        <li><button id="bookmark-button">북마크 보기</button></li>
        <li><button id="bookmark-group-button">북마크 그룹 관리</button></li>
    </ul>
</nav>
<form id="edit-group-form" action="${pageContext.request.contextPath}/bookmarkGroup" method="post" accept-charset="UTF-8">
    <input type="hidden" id="group-id" name="group-id" value="${group.id}">
    <table>
        <colgroup>
            <col style="width: 400px">
        </colgroup>
        <tr>
            <th><label for="group-name">북마크 그룹 이름</label></th>
            <td><input type="text" id="group-name" name="group-name" value="${group.name}" required></td>
        </tr>
        <tr>
            <th><label for="group-order">순서</label></th>
            <td><input type="number" id="group-order" name="group-order" value="${group.orderNumber}" required></td>
        </tr>
        <tr>
            <td colspan="2" class="centered">
                <button type="button" id="back-button">돌아가기</button>
                <button type="submit" name="action" value="edit">수정</button>
            </td>
        </tr>
    </table>
</form>
<script src="${pageContext.request.contextPath}/js/scripts.js" defer></script>
</body>
</html>
