package demo;

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/message"})
public class MessageController {
	@GetMapping(
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public MessageBoundary createGenericMessage() {
		MessageBoundary rv = new MessageBoundary();
		rv.setMessage("Hello World!");
		rv.setCreatedTimestamp(new Date());

		System.err.println("*** " + rv);
		
		return rv;
	}

	@GetMapping(
		path = {"/{name}"},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public MessageBoundary createPersonalizedMessage(
			@PathVariable("name") String name) {
		MessageBoundary rv = new MessageBoundary();
		if (name == null || name.trim().isEmpty()) {
			name = "Anonymous";
		}
		rv.setMessage("Hello " + name + "!");
		rv.setCreatedTimestamp(new Date());

		System.err.println("*** " + rv);
		
		return rv;
	}
	
	@GetMapping(
		path = {"/many/{count}"},
		produces = {MediaType.APPLICATION_JSON_VALUE})
	public MessageBoundary[] createManyMessages(
			@PathVariable("count") int count) {
		if (count < 1 || count > 10) {
			throw new RuntimeException("Requested count must be between 1 and 10");
		}
		
		List<MessageBoundary> list = 
		  IntStream.range(0, count)
			.map(i->i+1)
			.mapToObj(i->{
				MessageBoundary rv = new MessageBoundary("Message #" + i);
				
				return rv;
			})
			.toList();
		
		System.err.println("*** " + list);
		
		return list.toArray(new MessageBoundary[0]);
	}

}
