<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>북마크 그룹 목록</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <h1>북마크 그룹 목록</h1>
    <nav>
        <ul class="nav-menu">
            <li><button id="home-button">홈</button></li>
            <li><button id="history-button">위치 히스토리 목록</button></li>
            <li><button id="fetch-data-button">Open API 와이파이 정보 가져오기</button></li>
            <li><button id="bookmark-button">북마크 보기</button></li>
            <li><button id="bookmark-group-button">북마크 그룹 관리</button></li>
        </ul>
    </nav>
    <div>
        <button id="add-group-button">북마크 그룹 이름 추가</button>
    </div>

    <table id="bookmark-group-table">
        <thead>
            <tr>
                <th>ID</th>
                <th>북마크 이름</th>
                <th>순서</th>
                <th>등록일자</th>
                <th>수정일자</th>
                <th>비고</th>
            </tr>
        </thead>
        <tbody id="bookmark-group-table-tbody">
            <tr>
                <td colspan="6" class="centered">정보가 존재하지 않습니다.</td>
            </tr>
        </tbody>
    </table>
    <script src="${pageContext.request.contextPath}/js/scripts.js" defer></script>
</body>
</html>