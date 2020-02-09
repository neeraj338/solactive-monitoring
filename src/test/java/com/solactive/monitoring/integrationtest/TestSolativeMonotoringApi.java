package com.solactive.monitoring.integrationtest;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import com.solactive.monitoring.SolactiveMonitoringApplication;
import com.solactive.monitoring.model.Statistics;
import com.solactive.monitoring.model.Tick;
import com.solactive.monitoring.service.StatisticsService;
import com.solactive.monitoring.service.TickService;
import com.solactive.monitoring.util.SolactiveUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SolactiveMonitoringApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = { "test" })
public class TestSolativeMonotoringApi {
	
	private static String[] instrumentArr = new String[] {"IBM_N", "BOSE", "SONY", "PHLIP", "KLIPSCH", "PIONER"
			, "LEECO", "MS","TESLA", "HARMAN", "LUNNOR", "LISP", "JAVA", "ANT", "APACHE"};
	
	
	
	private static ExecutorService executor = Executors.newFixedThreadPool(4);
	
	@LocalServerPort
	int randomServerPort;

	@Autowired
	private  TestRestTemplate testRestTemplate;
	
	
	@Autowired
	private  TickService tickService;
	
	@Autowired
	private  StatisticsService statisticsService;
	
	@Test
	public void testBulkPost() {
		CompletableFuture<ResponseEntity<Object>>[]  task = new CompletableFuture[50];
		for(int i=0;i<50;i++) {
			CompletableFuture<ResponseEntity<Object>> c= CompletableFuture.supplyAsync(()->{
				
				
				ResponseEntity<Object> exchange = testRestTemplate.exchange(
						UriComponentsBuilder.fromUriString("/ticks").buildAndExpand(new HashMap<>()).toUri(), HttpMethod.POST,
						SolactiveUtil.getEnityWithHttpHeader(Tick.builder()
								.price(Math.random() * Double.MAX_VALUE)
								.instrument(instrumentArr[(int) (Math.random() * instrumentArr.length - 1)])
								.price(Math.random()).timestamp(System.currentTimeMillis()).build()

						), Object.class);
				
				Matcher<HttpStatus> equalToCreated = Matchers.equalTo(HttpStatus.CREATED);
				Matcher<HttpStatus> equalToNoContent = Matchers.equalTo(HttpStatus.NO_CONTENT);
				Assert.assertThat("success 201 or 204 if NO_CONTENT", exchange.getStatusCode(), Matchers.anyOf(equalToNoContent, equalToCreated));
				
				return exchange;
			}, executor);
					
			task[i] = c;
		}
		CompletableFuture.allOf(task)
		.thenRun(()->{
			ResponseEntity<Statistics> exchange = this.testRestTemplate.exchange(
					UriComponentsBuilder.fromUriString("/statistics").buildAndExpand(new HashMap<>()).toUri()
					, HttpMethod.GET
					, SolactiveUtil.getHttpHeader(), Statistics.class);
			
			Matcher<HttpStatus> statusOk = Matchers.equalTo(HttpStatus.OK);
			// Matcher<HttpStatus> statusNoContent = Matchers.equalTo(HttpStatus.NO_CONTENT);
			Statistics stat = exchange.getBody();
			Assert.assertThat("success 200 or 204 if NO_CONTENT", exchange.getStatusCode(), Matchers.anyOf(statusOk, statusOk));
			Assert.assertThat("statistics count > 0", stat.getCount().intValue(), Matchers.greaterThan(0));
		});
	}
	
	@Test
	public void testPostTickesSuccess() {
		
		ResponseEntity<Object> exchange = this.testRestTemplate.exchange(
				UriComponentsBuilder.fromUriString("/ticks").buildAndExpand(new HashMap<>()).toUri(), HttpMethod.POST,
				SolactiveUtil.getEnityWithHttpHeader(Tick.builder()
						.price(Math.random() * Double.MAX_VALUE)
						.instrument(instrumentArr[(int) (Math.random() * instrumentArr.length - 1)])
						.price(Math.random()).timestamp(System.currentTimeMillis()).build()

				), Object.class);
		
		Matcher<HttpStatus> equalToCreated = Matchers.equalTo(HttpStatus.CREATED);
		// Matcher<HttpStatus> equalToNoContent = Matchers.equalTo(HttpStatus.NO_CONTENT);
		Assert.assertThat("success 201 or 204 if NO_CONTENT", exchange.getStatusCode(), Matchers.anyOf(equalToCreated, equalToCreated));
	}
	
	@Test
	public void testPostTickesNoContent() {
		
		 Instant before60Sec = Instant.now().minus(Duration.ofSeconds(60));
		 
		ResponseEntity<Object> exchange = this.testRestTemplate.exchange(
				UriComponentsBuilder.fromUriString("/ticks").buildAndExpand(new HashMap<>()).toUri(), HttpMethod.POST,
				SolactiveUtil.getEnityWithHttpHeader(Tick.builder()
						.price(Math.random() * Double.MAX_VALUE)
						.instrument(instrumentArr[(int) (Math.random() * instrumentArr.length - 1)])
						.price(Math.random()).timestamp(before60Sec.getEpochSecond()).build()

				), Object.class);
		
		Matcher<HttpStatus> equalToNoContent = Matchers.equalTo(HttpStatus.NO_CONTENT);
		Assert.assertThat("success 201 or 204 if NO_CONTENT", exchange.getStatusCode(), Matchers.anyOf(equalToNoContent, equalToNoContent));
	}
	
	
	@Test
	public void testGetOverAllStatistice() {
		
		testPostTickesSuccess();
		ResponseEntity<Statistics> exchange = this.testRestTemplate.exchange(
				UriComponentsBuilder.fromUriString("/statistics").buildAndExpand(new HashMap<>()).toUri()
				, HttpMethod.GET
				, SolactiveUtil.getHttpHeader(), Statistics.class);
		
		Matcher<HttpStatus> statusOk = Matchers.equalTo(HttpStatus.OK);
		// Matcher<HttpStatus> statusNoContent = Matchers.equalTo(HttpStatus.NO_CONTENT);
		Statistics stat = exchange.getBody();
		Assert.assertThat("success 200 or 204 if NO_CONTENT", exchange.getStatusCode(), Matchers.anyOf(statusOk, statusOk));
		Assert.assertThat("statistics count > 0", stat.getCount().intValue(), Matchers.greaterThan(0));
	}
	
	@Test
	public void testGetStatisticesForOneTick() {
		
		int rand = (int) (Math.random() * instrumentArr.length - 1);
		//post one 
		this.testRestTemplate.exchange(
				UriComponentsBuilder.fromUriString("/ticks").buildAndExpand(new HashMap<>()).toUri(), HttpMethod.POST,
				SolactiveUtil.getEnityWithHttpHeader(Tick.builder()
						.price(Math.random() * Double.MAX_VALUE)
						.instrument(instrumentArr[rand])
						.price(Math.random()).timestamp(System.currentTimeMillis()).build()

				), Object.class);
		
		
		HashMap<String, Object> uriVariables = new HashMap<>();
		
		uriVariables.put("instrument_identifier", instrumentArr[rand]);
		ResponseEntity<Statistics> getexchange = this.testRestTemplate.exchange(
				UriComponentsBuilder.fromUriString("/statistics/{instrument_identifier}").buildAndExpand(uriVariables).toUri()
				, HttpMethod.GET
				, SolactiveUtil.getHttpHeader(), Statistics.class);
		
		Matcher<HttpStatus> statusOk = Matchers.equalTo(HttpStatus.OK);
		// Matcher<HttpStatus> statusNoContent = Matchers.equalTo(HttpStatus.NO_CONTENT);
		Statistics stat = getexchange.getBody();
		Assert.assertThat("success 200 or 204 if NO_CONTENT", getexchange.getStatusCode(), Matchers.anyOf(statusOk, statusOk));
		Assert.assertThat("statistice count > 0", stat.getCount().intValue(), Matchers.greaterThan(0));
	}
	
	@Test
	@Ignore
	public void testPostTicks() {
		for(int i=0;i<9999999;i++) {
			this.tickService.saveTick(Tick.builder()
						.price(Math.random() * Double.MAX_VALUE)
						.instrument(instrumentArr[(int) (Math.random() * instrumentArr.length - 1)])
						.price(Math.random()).timestamp(System.currentTimeMillis()).build());
			if(i%10 == 0) {
				sleep();
			}
			
		}
		Assert.assertThat("statistice count > 0", this.statisticsService.getInstrumentStatistics().get().getCount().intValue(), Matchers.greaterThan(0));
	}
    
    private static void sleep() {
		try {
			TimeUnit.MILLISECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
