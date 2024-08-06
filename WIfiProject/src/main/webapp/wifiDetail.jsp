<%@ page import="org.example.wifiproject.WifiInfo" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>와이파이 상세 정보</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<h1>와이파이 상세 정보</h1>
<nav>
    <ul class="nav-menu">
        <li><button id="home-button">홈</button></li>
        <li><button id="history-button">위치 히스토리 목록</button></li>
        <li><button id="fetch-data-button">Open API 와이파이 정보 가져오기</button></li>
        <li><button id="bookmark-button">북마크 보기</button></li>
        <li><button id="bookmark-group-button">북마크 그룹 관리</button></li>
    </ul>
</nav>
<%
    WifiInfo wifi = (WifiInfo) request.getAttribute("wifi");
    List<String> bookmarkGroups = (List<String>) request.getAttribute("bookmarkGroups");
    if (wifi == null) {
        out.println("wifi 객체가 null 입니다");
    } else {
%>

<div>
    <select id="bookmark-group-select">
        <option value="" disabled selected>북마크 그룹 이름 선택</option>
        <% if (bookmarkGroups != null && !bookmarkGroups.isEmpty()) {
            for (String group : bookmarkGroups) { %>
        <option value="<%= group %>"><%= group %></option>
        <% }
        } else { %>
        <option disabled>북마크 그룹을 먼저 생성해주세요.</option>
        <% } %>
    </select>
    <input type="hidden" id="wifi-name" value="<%= wifi.getX_SWIFI_MAIN_NM() %>">
    <input type="hidden" id="wifi-id" value="<%= wifi.getId() %>">
    <button onclick="addBookmarkGroup()">북마크 추가하기</button>
</div>

<table class="table">
    <colgroup>
        <col style="width: 400px">
    </colgroup>
    <tr>
        <th>거리 (Km)</th>
        <td><%= wifi.getFormattedDistance() %></td>
    </tr>
    <tr>
        <th>관리번호</th>
        <td><%= wifi.getX_SWIFI_MGR_NO() %></td>
    </tr>
    <tr>
        <th>자치구</th>
        <td><%= wifi.getX_SWIFI_WRDOFC() %></td>
    </tr>
    <tr>
        <th>와이파이명</th>
        <td><%= wifi.getX_SWIFI_MAIN_NM() %></td>
    </tr>
    <tr>
        <th>도로명주소</th>
        <td><%= wifi.getX_SWIFI_ADRES1() %></td>
    </tr>
    <tr>
        <th>상세주소</th>
        <td><%= wifi.getX_SWIFI_ADRES2() %></td>
    </tr>
    <tr>
        <th>설치위치 (층)</th>
        <td><%= wifi.getX_SWIFI_INSTL_FLOOR() %></td>
    </tr>
    <tr>
        <th>설치유형</th>
        <td><%= wifi.getX_SWIFI_INSTL_TY() %></td>
    </tr>
    <tr>
        <th>설치기관</th>
        <td><%= wifi.getX_SWIFI_INSTL_MBY() %></td>
    </tr>
    <tr>
        <th>서비스구분</th>
        <td><%= wifi.getX_SWIFI_SVC_SE() %></td>
    </tr>
    <tr>
        <th>망종류</th>
        <td><%= wifi.getX_SWIFI_CMCWR() %></td>
    </tr>
    <tr>
        <th>설치년도</th>
        <td><%= wifi.getX_SWIFI_CNSTC_YEAR() %></td>
    </tr>
    <tr>
        <th>실내외구분</th>
        <td><%= wifi.getX_SWIFI_INOUT_DOOR() %></td>
    </tr>
    <tr>
        <th>WIFI접속환경</th>
        <td><%= wifi.getX_SWIFI_REMARS3() %></td>
    </tr>
    <tr>
        <th>X좌표</th>
        <td><%= wifi.getLAT() %></td>
    </tr>
    <tr>
        <th>Y좌표</th>
        <td><%= wifi.getLNT() %></td>
    </tr>
    <tr>
        <th>작업일자</th>
        <td><%= wifi.getWORK_DTTM() %></td>
    </tr>
</table>
<%
    }
%>
<script src="${pageContext.request.contextPath}/js/scripts.js" defer></script>
</body>
</html>
