<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.FeedbackDAO" %>
<% 
	request.setCharacterEncoding("utf-8");

	String uid = request.getParameter("id");
	String ucon = request.getParameter("content");
	
	FeedbackDAO dao = new FeedbackDAO();
	
	if(dao.insert(uid, ucon)){
		out.print("OK");
	}
	else {
		out.print("ER");
	}
	
%>