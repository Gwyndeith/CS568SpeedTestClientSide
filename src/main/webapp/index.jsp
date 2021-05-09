<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<style type="text/css">
    .home-page {
        align-content: center;
        align-items: center;
        align-self: center;
        text-align: center;
        vertical-align: center;
    }

    .page-title {
        text-align: center;
    }

    /*
    .progress-div {
        text-align: center;
    }

    .progress-label {
        font-weight: bold;
    }
    */

    .section-title {
        text-align: center;
        align-content: center;
        align-self: center;
        align-items: center;
    }

    .test-start-button {
        align-self: center;
        background-color: cornflowerblue;
        border-radius: 8px;
        height: 40px;
        width: 200px;
    }
</style>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>CS568 Speed Test</title>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.js" type="text/javascript"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js" type="text/javascript"></script>
    </head>
    <script type="text/javascript">
        function startSpeedTest() {
            window.location.href = "${pageContext.request.contextPath}/speed-test-servlet";
        }
    </script>
    <body>
        <div class="home-page">
            <h1 class="page-title">Welcome to our Speed Test Application</h1>
            <h2 class="section-title">To begin your speed test, please click the button below:</h2>
            <button id="startSpeedTestButton" class="test-start-button" onclick="startSpeedTest()">Start Speed Test</button>
        </div>
        <br/>
        <!--
        <div class="progress-div">
            <label class="progress-label">Test progress</label>
            <br/>
            <progress id="progressBar" value="0" max="14"></progress>
        </div>
        -->
    </body>
</html>