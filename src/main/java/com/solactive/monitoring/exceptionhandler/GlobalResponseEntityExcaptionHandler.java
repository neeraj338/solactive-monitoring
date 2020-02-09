package com.solactive.monitoring.exceptionhandler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solactive.monitoring.util.SolactiveUtil;

@ControllerAdvice(annotations = {RestController.class})
public class GlobalResponseEntityExcaptionHandler extends ResponseEntityExceptionHandler{
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// error handle for @Valid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {

		logger.error(ExceptionUtils.getStackTrace(ex));

        Map<String, ObjectNode> messageMap = new LinkedHashMap<>();
        //Get all errors
        ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .forEach(x->messageMap.put(x.getField(), createObjNode(x.getField(), x.getDefaultMessage()) ) );
        
        ObjectNode errorJsonNode = SolactiveUtil.createErrorJsonNode(status
        		, ((ServletWebRequest)request).getRequest().getRequestURI().toString()
        		, messageMap);
        
        return new ResponseEntity<>(errorJsonNode, headers, status);

    }
    
    private static ObjectNode createObjNode(String filed, String message) {
    	ObjectNode jsonNode = SolactiveUtil.createJsonNode();
    	jsonNode.put("field", filed);
    	jsonNode.put("message", message);
    	return jsonNode;
    }
}
