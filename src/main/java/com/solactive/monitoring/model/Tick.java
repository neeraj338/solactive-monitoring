package com.solactive.monitoring.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
public class Tick implements Comparable<Tick>, Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	@EqualsAndHashCode.Include
	@NotBlank
	private String instrument;
	
	@NotNull
    private Double price;
    
    @NotNull
    private Long timestamp;
    
	@Override
	public int compareTo(Tick otherTick) {
		return this.timestamp.compareTo(otherTick.timestamp);
	}
	
}
