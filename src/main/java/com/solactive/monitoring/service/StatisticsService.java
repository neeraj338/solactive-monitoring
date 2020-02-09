package com.solactive.monitoring.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.solactive.monitoring.model.Statistics;
import com.solactive.monitoring.model.Tick;

@Service
public class StatisticsService{

	private static final ReentrantReadWriteLock rwlock = new ReentrantReadWriteLock();
	
	private Statistics sixtySecStatistics;
	
	private Map<String, Statistics> tickIdentifierStatMap = new ConcurrentHashMap<String, Statistics>();
	
	public Optional<Statistics> getInstrumentStatistics() {
		
		rwlock.readLock().lock();
		try{
			return sixtySecStatistics == null || sixtySecStatistics.getCount() == 0 ? Optional.empty() : Optional.of(sixtySecStatistics);
		}finally {
			rwlock.readLock().unlock();
		}
	}
	
	public Optional<Statistics> getStatisticsByInstrument(String instrument) {
		return Optional.ofNullable(tickIdentifierStatMap.getOrDefault(instrument, null));
	}

	@Async(value = "statisticsTaskExecutor")
	public void updateStatistics(Map<String, List<Tick>> tickIdentifierMultiMap) {
		
		if(tickIdentifierMultiMap.isEmpty()) {
			tickIdentifierStatMap.clear();
		}
		
		Statistics computedStat = Statistics.createStatistics(tickIdentifierMultiMap.values().stream().flatMap(x->x.stream()));
		
		rwlock.writeLock().lock();
		try {
			sixtySecStatistics = computedStat;
			//chm : removeIf EntrySetView, removeIf not thread safe, usages default
			tickIdentifierStatMap.entrySet().removeIf(x-> !tickIdentifierMultiMap.keySet().contains(x.getKey()));
		}finally {
			rwlock.writeLock().unlock();
		}
		for(Map.Entry<String, List<Tick>> entry: tickIdentifierMultiMap.entrySet()) {
			tickIdentifierStatMap.put(entry.getKey(), Statistics.createStatistics(entry.getValue().stream()));
		}
		
	}

}
