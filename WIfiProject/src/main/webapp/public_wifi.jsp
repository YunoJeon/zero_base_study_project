<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>와이파이 정보 구하기</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <script>
        var contextPath = "${pageContext.request.contextPath}";
    </script>
</head>
<body>
<h1>와이파이 정보 구하기</h1>
<nav>
    <ul class="nav-menu">
        <li><button id="home-button">홈</button></li>
        <li><button id="history-button">위치 히스토리 목록</button></li>
        <li><button id="fetch-data-button">Open API 와이파이 정보 가져오기</button></li>
        <li><button id="bookmark-button">북마크 보기</button></li>
        <li><button id="bookmark-group-button">북마크 그룹 관리</button></li>
    </ul>
</nav>

<div class="location-inputs">
    <label for="lat-input">LAT:</label>
    <input type="text" id="lat-input" placeholder="0.0">
    <label for="lnt-input">, LNT:</label>
    <input type="text" id="lnt-input" placeholder="0.0">
    <button id="get-location">내 위치 가져오기</button>

    <form id="nearby-wifi-form" method="get" action="${pageContext.request.contextPath}/nearbyWifi">
        <input type="hidden" id="lat" name="lat">
        <input type="hidden" id="lnt" name="lnt">
        <button type="submit" id="fetch-nearby-wifi-button">근처 WIFI 정보 보기</button>
    </form>
</div>

<table class="table">
    <colgroup>
        <col style="width: 86px">
        <col style="width: 77px">
        <col style="width: 74px">
        <col style="width: 147px">
        <col style="width: 197px">
        <col style="width: 127px">
        <col style="width: 83px">
        <col style="width: 134px">
        <col style="width: 99px">
        <col style="width: 144px">
        <col style="width: 104px">
        <col style="width: 59px">
        <col style="width: 73px">
        <col style="width: 116px">
        <col style="width: 97px">
        <col style="width: 103px">
        <col style="width: 113px">
    </colgroup>
    <thead>
    <tr>
        <th>거리(Km)</th>
        <th>관리번호</th>
        <th>자치구</th>
        <th>와이파이명</th>
        <th>도로명주소</th>
        <th>상세주소</th>
        <th>설치위치(층)</th>
        <th>설치유형</th>
        <th>설치기관</th>
        <th>서비스구분</th>
        <th>망종류</th>
        <th>설치년도</th>
        <th>실내외구분</th>
        <th>WIFI접속환경</th>
        <th>X좌표</th>
        <th>Y좌표</th>
        <th>작업일자</th>
    </tr>
    </thead>
    <tbody id="wifi-results">
    <!-- 데이터가 여기에 동적으로 삽입 -->
    </tbody>
    <tfoot>
    <tr id="no-results">
        <td colspan="17">위치 정보를 입력한 후에 조회해 주세요.</td>
    </tr>
    </tfoot>
</table>
<script src="${pageContext.request.contextPath}/js/scripts.js" defer></script>
</body>
</html>