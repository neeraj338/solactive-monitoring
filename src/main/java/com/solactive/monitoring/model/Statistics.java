package com.solactive.monitoring.model;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonInclude(JsonInclude.Include.ALWAYS)
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Statistics implements Serializable{
	
	private static final long serialVersionUID = 1L;

    private Double avg;

    private Double max;

    private Double min;

    private Long count;


    public static  Statistics createStatistics(Stream<Tick> tickStream) {
    	Statistics stat = new Statistics();;
        final List<Double> amountsLastMinute = tickStream.map(Tick::getPrice).collect(Collectors.toList());
        final Long count = amountsLastMinute.stream().count();
        stat.setCount(count);
        if (count > 0) {
        	
        	stat.setAvg(amountsLastMinute.stream().mapToDouble(Double::doubleValue).average().getAsDouble());
        	stat.setMax(amountsLastMinute.stream().max(Double::compareTo).get());
            stat.setMin(amountsLastMinute.stream().min(Double::compareTo).get());
            
        }
        return stat;
    }
    
}
