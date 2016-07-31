package com.ecommerce.flashsales;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class FlashSalesAccessLogger {
	private final Logger accessLogger = LoggerFactory.getLogger("AccessLog");
	public class FlashSalesAccessLog{
		public String timesTamp;
		public String requestIP;
		public String requestPort;
		public String serverIP;
		public String serverPort;
		public String sessionID;
		/***
		 * the step for the current request
		 * 0-Client Requesting,1-Safe Protector Service,2-Policy Controller Service
		 * 3-Inventory Manager Service,4-Shopping Cart Service，5-All Steps
		 */
		public String currentStep;
		public String requestURL;
		public String requestParams;
		public String requestMethod;
		public String httpStatusCode;
		public String processingTime;
		public String responseBody;
		public String userAgent;
		
		public String getTimesTamp() {
			return timesTamp;
		}
		public void setTimesTamp(String timesTamp) {
			this.timesTamp = timesTamp;
		}
		public String getRequestIP() {
			return requestIP;
		}
		public void setRequestIP(String requestIP) {
			this.requestIP = requestIP;
		}
		public String getRequestPort() {
			return requestPort;
		}
		public void setRequestPort(String requestPort) {
			this.requestPort = requestPort;
		}
		public String getServerIP() {
			return serverIP;
		}
		public void setServerIP(String serverIP) {
			this.serverIP = serverIP;
		}
		public String getServerPort() {
			return serverPort;
		}
		public void setServerPort(String serverPort) {
			this.serverPort = serverPort;
		}
		public String getSessionID() {
			return sessionID;
		}
		public void setSessionID(String sessionID) {
			this.sessionID = sessionID;
		}
		public String getCurrentStep() {
			return currentStep;
		}
		public void setCurrentStep(String currentStep) {
			this.currentStep = currentStep;
		}
		
		public String getHttpStatusCode() {
			return httpStatusCode;
		}
		public void setHttpStatusCode(String httpStatusCode) {
			this.httpStatusCode = httpStatusCode;
		}
		public String getProcessingTime() {
			return processingTime;
		}
		public void setProcessingTime(String processingTime) {
			this.processingTime = processingTime;
		}
		public String getResponseBody() {
			return responseBody;
		}
		public void setResponseBody(String responseBody) {
			this.responseBody = responseBody;
		}
		public String getUserAgent() {
			return userAgent;
		}
		public void setUserAgent(String userAgent) {
			this.userAgent = userAgent;
		}
		public String getRequestURL() {
			return requestURL;
		}
		public void setRequestURL(String requestURL) {
			this.requestURL = requestURL;
		}
		
		public String getRequestParams() {
			return requestParams;
		}
		public void setRequestParams(String requestParams) {
			this.requestParams = requestParams;
		}
		public String getRequestMethod() {
			return requestMethod;
		}
		public void setRequestMethod(String requestMethod) {
			this.requestMethod = requestMethod;
		}
		@Override
		public String toString() {
			return "FlashSalesAccessLog [timesTamp=" + timesTamp + ", requestIP=" + requestIP + ", requestPort="
					+ requestPort + ", serverIP=" + serverIP + ", serverPort=" + serverPort + ", sessionID=" + sessionID
					+ ", currentStep=" + currentStep + ", requestURL=" + requestURL + ", requestParams=" + requestParams
					+ ", requestMethod=" + requestMethod + ", httpStatusCode=" + httpStatusCode + ", processingTime="
					+ processingTime + ", responseBody=" + responseBody + ", userAgent=" + userAgent + "]";
		}
		
	}
	public void doAccessLog(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String sessionID, String currentStep, String requestParams, long processTime, Object msgBody) throws JsonProcessingException{
		FlashSalesAccessLog flashSalesAccessLog = new FlashSalesAccessLog();
		String finalMsg = null;
		ObjectMapper mapper = new ObjectMapper();
		String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		flashSalesAccessLog.setTimesTamp(timestamp);
		flashSalesAccessLog.setRequestIP(httpRequest.getRemoteAddr());
		flashSalesAccessLog.setRequestPort(String.valueOf(httpRequest.getRemotePort()));
		flashSalesAccessLog.setServerIP(httpRequest.getLocalAddr());
		flashSalesAccessLog.setServerPort(String.valueOf(httpRequest.getLocalPort()));
		flashSalesAccessLog.setSessionID(sessionID);
		flashSalesAccessLog.setCurrentStep(currentStep);
		flashSalesAccessLog.setRequestURL(httpRequest.getRequestURL().toString());
		flashSalesAccessLog.setRequestParams(requestParams);
		flashSalesAccessLog.setRequestMethod(httpRequest.getMethod());
		flashSalesAccessLog.setProcessingTime(String.valueOf(processTime));
		flashSalesAccessLog.setHttpStatusCode(String.valueOf(httpResponse.getStatus()));
		
		/*** convert msgBody to json string */
		flashSalesAccessLog.setResponseBody(mapper.writeValueAsString(msgBody));
		flashSalesAccessLog.setUserAgent(httpRequest.getHeader("user-agent"));

		/*** convert flashSalesAccessLog to json string */
		finalMsg = mapper.writeValueAsString(flashSalesAccessLog).replace("\\", "").replace("\"{", "{").replace("}\"", "}");
		accessLogger.info(finalMsg);
	}
}
