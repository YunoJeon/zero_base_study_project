document.addEventListener('DOMContentLoaded', function () {
    const contextPath = '/WIfiProject_war_exploded';

    const getLocationBtn = document.getElementById('get-location');
    if (getLocationBtn) {
        getLocationBtn.addEventListener('click', function () {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function (position) {
                    const lat = position.coords.latitude;
                    const lnt = position.coords.longitude;

                    document.getElementById('lat').value = lat;
                    document.getElementById('lnt').value = lnt;
                    document.getElementById('lat-input').value = lat;
                    document.getElementById('lnt-input').value = lnt;

                    console.log('Latitude:', lat);
                    console.log('Longitude:', lnt);

                }, function (error) {
                    console.error('Error getting location:', error);
                    alert('위치 정보를 가져오는 데 실패했습니다.');
                });
            } else {
                alert('이 브라우저는 Geolocation을 지원하지 않습니다.');
            }
        });
    }

    const homeButton = document.getElementById('home-button');
    if (homeButton) {
        homeButton.addEventListener('click', function () {
            window.location.href = `${contextPath}/public_wifi.jsp`;
        });
    }

    const historyButton = document.getElementById('history-button');
    if (historyButton) {
        historyButton.addEventListener('click', function () {
            window.location.href = `${contextPath}/history.jsp`;
        });
    }

    const fetchDataButton = document.getElementById('fetch-data-button');
    if (fetchDataButton) {
        fetchDataButton.addEventListener('click', function () {
            const lat = document.getElementById('lat') ? document.getElementById('lat').value : '';
            const lnt = document.getElementById('lnt') ? document.getElementById('lnt').value : '';

            window.location.href = `${contextPath}/fetchData?lat=${encodeURIComponent(lat)}&lnt=${encodeURIComponent(lnt)}`;
        });
    }

    const fetchNearbyWifiButton = document.getElementById('fetch-nearby-wifi-button');
    if (fetchNearbyWifiButton) {
        fetchNearbyWifiButton.addEventListener('click', function (event) {
            event.preventDefault();

            const lat = document.getElementById('lat-input').value;
            const lnt = document.getElementById('lnt-input').value;

            if (lat && lnt) {
                fetchNearbyWifi(lat, lnt);
            } else {
                alert('위치 정보를 먼저 가져와야 합니다.');
            }
        });
    }

    const bookmarkButton = document.getElementById('bookmark-button');
    if (bookmarkButton) {
        bookmarkButton.addEventListener('click', function () {
            window.location.href = `${contextPath}/showBookmark.jsp`;
        });
    }

    const bookmarkGroupButton = document.getElementById('bookmark-group-button');
    if (bookmarkGroupButton) {
        bookmarkGroupButton.addEventListener('click', function () {
            window.location.href = `${contextPath}/bookmarkGroup.jsp`;
        });
    }

    const backButton = document.getElementById('back-button');
    if (backButton) {
        backButton.addEventListener('click', function () {
            window.history.back();
        });
    }

    async function handleSubmitForm(event) {
        event.preventDefault();
        const formData = new FormData(document.getElementById('add-group-form'));
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(`${contextPath}/bookmarkGroup?action=add`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(data)
            });

            const responseText = await response.text();
            let jsonResponse;

            try {
                jsonResponse = JSON.parse(responseText);
            } catch (e) {
                throw new Error(`Failed to parse JSON: ${responseText}`);
            }

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}. Response: ${responseText}`);
            }

            if (jsonResponse.length > 0) {
                alert('북마크 그룹이 추가되었습니다.');
                window.location.href = `${contextPath}/bookmarkGroup.jsp`;
            } else {
                throw new Error('응답 데이터가 비어있습니다.');
            }
        } catch (error) {
            console.error('북마크 그룹 추가 중 오류가 발생하였습니다:', error);
            alert(`북마크 그룹 추가 중 오류가 발생하였습니다: ${error.message}`);
        }
    }

    async function handleEditForm(event) {
        event.preventDefault();
        const formData = new FormData(document.getElementById('edit-group-form'));
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch(`${contextPath}/bookmarkGroup?action=edit`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(data)
            });

            const responseText = await response.text();
            let jsonResponse;

            try {
                jsonResponse = JSON.parse(responseText);
            } catch (e) {
                throw new Error(`Failed to parse JSON: ${responseText}`);
            }

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}. Response: ${responseText}`);
            }

            if (jsonResponse.length > 0) {
                alert('북마크 그룹 정보가 수정되었습니다.');
                window.location.href = `${contextPath}/bookmarkGroup.jsp`;
            } else {
                throw new Error('응답 데이터가 비어있습니다.');
            }
        } catch (error) {
            console.error('북마크 그룹 수정 중 오류가 발생하였습니다:', error);
            alert(`북마크 그룹 수정 중 오류가 발생하였습니다: ${error.message}`);
        }
    }

    const form = document.getElementById('add-group-form');
    if (form) {
        form.addEventListener('submit', handleSubmitForm);
    }

    const editForm = document.getElementById('edit-group-form');
    if (editForm) {
        editForm.addEventListener('submit', handleEditForm);
    }

    const addGroupButton = document.getElementById('add-group-button');
    if (addGroupButton) {
        addGroupButton.addEventListener('click', function () {
            window.location.href = `${contextPath}/bookmarkGroupAdd.jsp`;
        });
    }

    function fetchBookmarkGroup() {
        fetch(`${contextPath}/bookmarkGroup?action=list`)
            .then(response => {
                console.log('Response status:', response.status);
                console.log('Response content type:', response.headers.get('Content-Type'));

                // 응답 텍스트를 먼저 확인
                return response.text().then(text => {
                    if (text) {
                        try {
                            const data = JSON.parse(text);
                            return data;
                        } catch (e) {
                            console.error('Failed to parse JSON:', e);
                            throw new Error('Failed to parse JSON');
                        }
                    } else {
                        throw new Error('Empty response');
                    }
                });
            })
            .then(data => {
                console.log('Fetched bookmark group data:', data);

                const tableBody = document.querySelector('#bookmark-group-table-tbody');
                if (tableBody) {
                    if (Array.isArray(data) && data.length > 0) {
                        tableBody.innerHTML = data.map(group => `
                        <tr>
                            <td>${group.ID}</td>
                            <td>${group.name}</td>
                            <td>${group.orderNumber}</td>
                            <td>${formatDate(group.createDate)}</td>
                            <td>${formatDate(group.modifiedDate)}</td>
                            <td>
                                <button onclick="editBookmarkGroup(${group.ID})">수정</button>
                                <button onclick="deleteBookmarkGroup(${group.ID})">삭제</button>
                            </td>
                        </tr>
                    `).join('');
                    } else {
                        tableBody.innerHTML = '<tr><td colspan="6" class="centered">정보가 존재하지 않습니다.</td></tr>';
                    }
                }
            })
            .catch(error => {
                console.error('Error fetching bookmark group:', error);
            });
    }

    function formatDate(dateObj) {
        if (!dateObj) return '';
        const date = dateObj.date || {};
        const time = dateObj.time || {};
        return `${(date.year || '')}-${(date.month || '')}-${(date.day || '')} ${(time.hour || '')}:${(time.minute || '')}:${(time.second || '')}`;
    }

    window.editBookmarkGroup = function (id) {
        window.location.href = `${contextPath}/bookmarkGroupEdit.jsp?id=${id}`;
    }
    const url = new URLSearchParams(window.location.search);
    const id = url.get('id');

    if (id) {
        fetch(`${contextPath}/bookmarkGroup?action=edit&id=${id}`)
            .then(response => response.json())
            .then(data => {
                const messageElement = document.getElementById('message');
                if (data) {
                    document.getElementById('group-id').value = data.ID;
                    document.getElementById('group-name').value = data.name;
                    document.getElementById('group-order').value = data.orderNumber;
                } else if (messageElement) {
                    messageElement.innerHTML = "그룹 정보를 로드할 수 없습니다.";
                }
            })
            .catch(error => {
                console.error('Error fetching bookmark group:', error);
                const messageElement = document.getElementById('message');
                if (messageElement) {
                    messageElement.innerHTML = "그룹 정보를 로드할 수 없습니다.";
                }
            });
    } else {
        const messageElement = document.getElementById('message');
        if (messageElement) {
            messageElement.innerHTML = "그룹 정보를 로드할 수 없습니다.";
        }
    }

    window.deleteBookmarkGroup = function (id) {
        if (confirm('정말로 삭제하시겠습니까?')) {
            fetch(`${contextPath}/bookmarkGroup?id=${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        console.log('삭제 성공');
                        alert('북마크 그룹이 삭제되었습니다.');
                        fetchBookmarkGroup(); // UI 업데이트
                    } else {
                        console.error('북마크 그룹 삭제 오류:', data.message);
                    }
                })
                .catch(error => {
                    console.error('북마크 그룹 삭제 중 오류:', error);
                });
        }
    }

    if (window.location.pathname.endsWith('/bookmarkGroup.jsp')) {
        fetchBookmarkGroup();
    }

    function fetchNearbyWifi(lat, lnt) {
        const url = `${contextPath}/nearbyWifi?lat=${lat}&lnt=${lnt}`;

        fetch(url)
            .then(response => response.json())
            .then(data => {
                const wifiResults = document.getElementById('wifi-results');

                console.log('Fetched data:', data);

                if (data.length > 0) {
                    wifiResults.innerHTML = data.map(wifi => `
                            <tr>
                                <td>${wifi.DISTANCE.toFixed(4)}</td>
                                <td>${wifi.X_SWIFI_MGR_NO}</td>
                                <td>${wifi.X_SWIFI_WRDOFC}</td>
                                <td><a href="${contextPath}/wifiDetail?id=${wifi.id}&lat=${lat}&lnt=${lnt}">${wifi.X_SWIFI_MAIN_NM}</a></td>
                                <td>${wifi.X_SWIFI_ADRES1}</td>
                                <td>${wifi.X_SWIFI_ADRES2}</td>
                                <td>${wifi.X_SWIFI_INSTL_FLOOR}</td>
                                <td>${wifi.X_SWIFI_INSTL_TY}</td>
                                <td>${wifi.X_SWIFI_INSTL_MBY}</td>
                                <td>${wifi.X_SWIFI_SVC_SE}</td>
                                <td>${wifi.X_SWIFI_CMCWR}</td>
                                <td>${wifi.X_SWIFI_CNSTC_YEAR}</td>
                                <td>${wifi.X_SWIFI_INOUT_DOOR}</td>
                                <td>${wifi.X_SWIFI_REMARS3}</td>
                                <td>${wifi.LAT}</td>
                                <td>${wifi.LNT}</td>
                                <td>${wifi.WORK_DTTM}</td>
                            </tr>
                        `).join('');
                    document.getElementById('no-results').style.display = 'none';
                    saveHistory(lat, lnt);
                } else {
                    wifiResults.innerHTML = '';
                    document.getElementById('no-results').style.display = 'table-row';
                    alert('와이파이 정보를 먼저 가져와야 합니다.');
                }
            })
            .catch(error => {
                console.error('Error fetching nearby WiFi:', error);
            });
    }

    function saveHistory(lat, lnt) {
        const url = `${contextPath}/saveHistory`;
        const data = new URLSearchParams();
        data.append('LAT', lat);
        data.append('LNT', lnt);

        fetch(url, {
            method: 'POST',
            body: data,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
        })
            .then(response => {
                if (response.ok) {
                    console.log('History saved successfully.');
                } else {
                    console.error('Failed to save history:', response.statusText);
                }
            })
            .catch(error => {
                console.error('Error saving history:', error);
            });
    }

    if (window.location.pathname.endsWith('/history.jsp')) {
        fetchHistory();
    }

    function fetchHistory() {
        fetch(`${contextPath}/history`)
            .then(response => {
                const contentType = response.headers.get('Content-Type');
                if (contentType && contentType.includes('application/json')) {
                    return response.json();
                } else {
                    return response.text().then(text => {
                        throw new Error(`Expected JSON but got ${contentType}: ${text}`);
                    });
                }
            })
            .then(data => {
                console.log('Fetched history data:', data);

                const historyTable = document.getElementById('history-results');
                if (data.length > 0) {
                    historyTable.innerHTML = data.map(entry => `
                    <tr>
                        <td>${entry.id}</td>
                        <td>${entry.lat}</td>
                        <td>${entry.lnt}</td>
                        <td>${entry.query_date}</td>
                        <td><button onclick="deleteHistory(${entry.id})">삭제</button></td>
                    </tr>
                `).join('');
                } else {
                    historyTable.innerHTML = '<tr><td colspan="5">저장된 위치 기록이 없습니다.</td></tr>';
                }
            })
            .catch(error => {
                console.error('Error fetching history:', error);
            });
    }

    window.deleteHistory = function (id) {
        if (confirm('정말로 삭제하시겠습니까?')) {
            fetch(`${contextPath}/deleteHistory?id=${id}`, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        return response.text();
                    } else {
                        throw new Error('Failed to delete history');
                    }
                })
                .then(data => {
                    console.log('Deleted history:', data);
                    alert('위치 기록이 삭제되었습니다.');
                    fetchHistory(); // 기록 목록을 새로고침
                })
                .catch(error => {
                    console.error('Error deleting history:', error);
                });
        }
    }

    function addBookmarkGroup() {
        const selectElement = document.getElementById('bookmark-group-select');
        const wifiId = document.getElementById('wifi-id').value
        const groupName = selectElement.value;
        const wifiName = document.getElementById('wifi-name').value;

        console.log('Wifi Name:', wifiName);
        console.log('Wifi ID:', wifiId);
        console.log('Group Name:', groupName);

        if (wifiId && groupName && wifiName) {
            fetch(`${contextPath}/wifiDetail?wifiId=${encodeURIComponent(wifiId)}&groupName=${encodeURIComponent(groupName)}&wifiName=${(encodeURIComponent(wifiName))}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    wifiId: wifiId,
                    groupName: groupName,
                    wifiName: wifiName
                })
            })
                .then(response => response.json())
                .then(result => {
                    if (result.success) {
                        alert('북마크가 추가되었습니다.');
                    } else {
                        alert('북마크 추가 실패.');
                    }
                })
                .catch(error => {
                    console.error('북마크 추가 중 오류:', error);
                    alert('북마크 추가 중 오류가 발생했습니다.');
                });
        } else {
            alert('북마크 그룹을 선택해 주세요.');
        }
    }
    window.addBookmarkGroup = addBookmarkGroup;

    function fetchBookmarkList() {
        fetch(`${contextPath}/showBookmark`)
            .then(response => response.json())
            .then(data => {
                console.log('Fetched bookmark group data:', data);

                const tableBody = document.querySelector('#bookmark-list-table-tbody');
                if (tableBody) {
                    if (Array.isArray(data) && data.length > 0) {
                        // 모든 북마크의 LAT와 LNT 값을 가져올 Promise 배열 생성
                        const promises = data.map(list => fetchLatLnt(list.wifi_id)
                            .then(({ lat, lnt }) => {
                                // LAT와 LNT 값을 포함한 HTML 템플릿 생성
                                return `
                                <tr>
                                    <td>${list.ID}</td>
                                    <td>${list.name}</td>
                                    <td><a href="${contextPath}/wifiDetail?id=${list.wifi_id}&lat=${lat}&lnt=${lnt}">${list.wifi_name}</a></td>
                                    <td>${formatDate(list.createDate)}</td>
                                    <td>
                                        <button onclick="deleteBookmarkList(${list.ID})">삭제</button>
                                    </td>
                                </tr>
                            `;
                            })
                        );

                        Promise.all(promises).then(rows => {
                            tableBody.innerHTML = rows.join('');
                        });
                    } else {
                        tableBody.innerHTML = '<tr><td colspan="5" class="centered">정보가 존재하지 않습니다.</td></tr>';
                    }
                }
            })
            .catch(error => {
                console.error('Error fetching bookmark list:', error);
            });
    }

    function fetchLatLnt(id) {
        return fetch(`${contextPath}/findWifi?id=${id}`)
            .then(response => response.json())
            .then(data => {
                if (data.lat && data.lnt) {
                    return { lat: data.lat, lnt: data.lnt };
                } else {
                    console.error('Invalid data received for id:', id);
                    return { lat: 0, lnt: 0 }; // 기본값 또는 에러 처리가 필요함
                }
            })
            .catch(error => {
                console.error('Error fetching lat/lnt:', error);
                return { lat: 0, lnt: 0 }; // 기본값 또는 에러 처리가 필요함
            });
    }

    window.deleteBookmarkList = function (id) {
        if (confirm('정말로 삭제하시겠습니까?')) {
            fetch(`${contextPath}/showBookmark?id=${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        console.log('삭제 성공');
                        alert('북마크가 삭제되었습니다.');
                        fetchBookmarkList(); // UI 업데이트
                    } else {
                        console.error('북마크 삭제 오류:', data.message);
                    }
                })
                .catch(error => {
                    console.error('북마크 삭제 중 오류:', error);
                });
        }
    }

    if (window.location.pathname.endsWith('/showBookmark.jsp')) {
        fetchBookmarkList();
    }
});