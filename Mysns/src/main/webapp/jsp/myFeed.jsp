<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dao.FeedDAO" %>
<%
	String uid = request.getParameter("id");
	out.print((new FeedDAO()).getMyGroup(uid));
%>