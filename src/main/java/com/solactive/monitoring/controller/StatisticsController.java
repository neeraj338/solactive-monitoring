package com.solactive.monitoring.controller;

import static org.springframework.http.HttpStatus.OK;

import java.util.Optional;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solactive.monitoring.model.Statistics;
import com.solactive.monitoring.service.StatisticsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/statistics", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
@Api("Operations on statistics")
@Validated
@CrossOrigin
public class StatisticsController {
	
	@Autowired
	private StatisticsService statisticsService;

	@ApiOperation(value = "get statistics ", response = Statistics.class)
	@GetMapping
	public ResponseEntity<Statistics> getTransactions() {
		Optional<Statistics> statistics = statisticsService.getInstrumentStatistics();
		if(statistics.isPresent()) {
			return new ResponseEntity<>(statistics.get(), OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@ApiOperation(value = "get statistics by instrument_identifier", response = Statistics.class)
	@GetMapping("/{instrument_identifier}")
	public ResponseEntity<Statistics> getStatisticsByIdentifier(
			@NotBlank @PathVariable(value = "instrument_identifier") String instrumentIdentifier) {
		Optional<Statistics> statistics = statisticsService.getStatisticsByInstrument(instrumentIdentifier);
		if(statistics.isPresent()) {
			return new ResponseEntity<>(statistics.get(), OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}