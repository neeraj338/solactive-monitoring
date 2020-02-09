package com.solactive.monitoring.controller;

import java.time.Duration;
import java.time.Instant;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solactive.monitoring.model.Tick;
import com.solactive.monitoring.service.TickService;
import com.solactive.monitoring.util.SolactiveUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/ticks", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
@Api("Operations on ticks")
@Validated
@CrossOrigin
public class TickController {
	
	@Autowired
	private TickService tickService;

	@ApiOperation(value = "create a tick")
	@PostMapping
	public ResponseEntity<?> createTick(@Valid @RequestBody Tick tick) {
		Instant sixtySecAgo = Instant.now().minus(Duration.ofSeconds(60));
		if(SolactiveUtil.isBeforeSeconds(tick.getTimestamp(), sixtySecAgo)) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
		}
		tickService.saveTick(tick);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
}
