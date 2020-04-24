<%--
  Created by IntelliJ IDEA.
  User: zwq
  Date: 2020/4/22
  Time: 11:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page isELIgnored="false" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- 新 Bootstrap 核心 CSS 文件 -->
<link href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
<html>
<head>
    <title>index</title>
</head>
<body>
<form  action="/resume">
    <table class="table table-bordered">
        <a class="btn btn-primary" href="/resume/insert">新增</a>
        <th>姓名</th>
        <th>地址</th>
        <th>电话</th>
        <th>操作</th>
        <c:forEach items="${list}" var="resume">
        <tr>
                <td>${resume.name}</td>
                <td>${resume.address}</td>
                <td>${resume.phone}</td>
            <td>
                <div><a class="btn btn-primary" href="/resume/resume?id=${resume.id}">修改</a></div>
                <div>
                <form method="post" action="/resume/resume" >
                    <input type="hidden" name="_method" value="DELETE"/>
                    <input type="hidden" name="id" value="${resume.id}"/>
                    <input class="btn btn-primary" type="submit" value="删除"/>
                </form>
                </div>
            </td>
        </tr>
        </c:forEach>

    </table>
</form>
</body>
</html>
