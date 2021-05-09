<%--
  Created by IntelliJ IDEA.
  User: Orkun
  Date: 9.05.2021
  Time: 01:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
  <style type="text/css">
    .result-page {
      align-content: center;
      align-items: center;
    }

    .page-title {
      text-align: center;
    }

    .result-list {
      text-align: center;
    }

    .button-div {
      text-align: center;
    }

    .test-start-button {
      align-self: center;
      background-color: cornflowerblue;
      border-radius: 8px;
      height: 40px;
      width: 200px;
    }
  </style>
  <script type="text/javascript">
    function returnToHome() {
      window.location.href = "index.jsp";
    }

    function reRunTest() {
      window.location.href = "${pageContext.request.contextPath}/speed-test-servlet";
    }
  </script>
  <head>
      <title>CS568 Speed Test Results</title>
  </head>
  <body>
    <div class="result-page">
      <h1 class="page-title">Speed Test Results</h1>
      <div class="result-list">
        <label>Ping: ${averagePing} ms</label>
        <br/>
        <label>Download speed: ${averageDownloadSpeed} Mbps</label>
        <br/>
        <label>Upload speed: ${averageUploadSpeed} Mbps</label>
        <br/>
        <br/>
      </div>
      <div class="button-div">
        <button class="test-start-button" onclick="reRunTest()">Run Speed Test Again</button>
        <br/>
        <button class="test-start-button" onclick="returnToHome()">Return Home</button>
      </div>
    </div>
  </body>
</html>
