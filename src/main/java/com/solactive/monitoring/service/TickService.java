package com.solactive.monitoring.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.solactive.monitoring.model.Tick;
import com.solactive.monitoring.util.SolactiveUtil;

@Service
public class TickService {
	
	private Map<String, List<Tick>> tickIdentifierMultiMap  = new ConcurrentHashMap<String, List<Tick>>();
	
	@Autowired
	private StatisticsService statService;
	
	@Async(value = "tickTaskExecutor")
	public void saveTick(Tick tick) {
		
		tickIdentifierMultiMap.computeIfAbsent(tick.getInstrument(), k -> new CopyOnWriteArrayList<Tick>()).add(tick);
		
		//update statistics
		clearUpdateStatEveryTwentySevenSec();
	}
	
	@Scheduled(cron = "0/27 * * * * *")
	public void clearUpdateStatEveryTwentySevenSec() {
		//1. clear stale data
		clearStaleData();
		//2. update statistics 
		statService.updateStatistics(tickIdentifierMultiMap);
	}
	
	private void clearStaleData() {
		Instant sixtySecAgo = Instant.now().minus(Duration.ofSeconds(60));
		
		Iterator<List<Tick>> iterator = tickIdentifierMultiMap.values().iterator();
		
		while(iterator.hasNext()) {
			iterator.next().removeIf(x-> SolactiveUtil.isBeforeSeconds(x.getTimestamp(), sixtySecAgo));
		}
		
		// remove entry has no value 
		tickIdentifierMultiMap.values().removeIf(x-> x == null || x.isEmpty()) ;
	}
}
