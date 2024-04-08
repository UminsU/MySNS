<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Random" %>

<%
    // 6자리 난수 생성
    Random random = new Random();
    String captchaText = String.format("%06d", random.nextInt(1000000));

    // 생성된 캡차 텍스트를 세션에 저장
    session.setAttribute("captchaText", captchaText);
    
    out.print(captchaText);
%>

