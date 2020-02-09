package com.solactive.monitoring.util;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SolactiveUtil {

	public static ObjectNode createJsonNode() {
		return JsonNodeFactory.instance.objectNode();
	}

	public static ObjectNode createErrorJsonNode(HttpStatus status, String uri, String message) {
		ObjectNode jsonNode = createJsonNode();
		jsonNode.putPOJO("timestamp", new Date());
		jsonNode.put("status", status.value());
		jsonNode.putPOJO("error", status.getReasonPhrase());
		jsonNode.put("path", uri);
		jsonNode.put("message", message);

		return jsonNode;
	}

	public static ObjectNode createErrorJsonNode(HttpStatus status, String uri, Map<String, ObjectNode> messageMap) {
		ObjectNode jsonNode = createJsonNode();
		jsonNode.putPOJO("timestamp", new Date());
		jsonNode.put("status", status.value());
		jsonNode.putPOJO("error", status.getReasonPhrase());
		jsonNode.put("path", uri);

		ArrayNode arrayJsonNode = jsonNode.putArray("messages");

		// Get all errors
		messageMap.forEach((k, v) -> arrayJsonNode.add(v));

		return jsonNode;
	}
	
	public static boolean isBeforeSeconds(long tickTimestamp, Instant secAgo) {
		Instant instant = Instant.ofEpochMilli(tickTimestamp);
	    return instant.isBefore(secAgo);
	}
	

	public static <T> HttpEntity<T> getEnityWithHttpHeader(T requestObject) {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<T> request = new HttpEntity<>(requestObject, headers);

		return request;
	}

	public static HttpEntity<?> getHttpHeader() {
		HttpHeaders headers = new HttpHeaders();

		HttpEntity<?> request = new HttpEntity<>(headers);

		return request;
	}

}
