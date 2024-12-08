package sas.controller;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sas.boundary.ObjectBoundary;

@RestController
@RequestMapping(path = {"/aii"})
public class ObjectController {
	
private String springApplicationName;
	
	@Value("${spring.application.name:defaultAppName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
		System.err.println("********" + this.springApplicationName);
	}
	
	@GetMapping(
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary createGenericObject() {
		ObjectBoundary rv = new ObjectBoundary();
		
		// add logic and mapping 

		System.err.println("*** " + rv);
		
		return rv;
	}

	@GetMapping(
		path = {"/{name}"},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary createPersonalizedMessage(
			@PathVariable("name") String name) {
		ObjectBoundary rv = new ObjectBoundary();
		if (name == null || name.trim().isEmpty()) {
			name = "Anonymous";
		}

		System.err.println("*** " + rv);
		
		return rv;
	}
	
	@GetMapping(
		path = {"/many/{count}"},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public ObjectBoundary[] createManyMessages(
			@PathVariable("count") int count) {
		if (count < 1 || count > 10) {
			throw new RuntimeException("Requested count must be between 1 and 10");
		}
		
		List<ObjectBoundary> list = 
		  IntStream.range(0, count)
			.map(i->i+1)
			.mapToObj(i->{
				ObjectBoundary rv = new ObjectBoundary("Message #" + i);
				
				return rv;
			})
			.toList();
		
		System.err.println("*** " + list);
		
		return list.toArray(new ObjectBoundary[0]);
	}


	

}




