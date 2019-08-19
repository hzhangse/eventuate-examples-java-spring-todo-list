package net.chrisrichardson.eventstore.examples.todolist.todoservice.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.persistence.EntityNotFoundException;

import org.apache.kafka.common.metrics.stats.Rate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import io.eventuate.CompletableFutureUtil;
import net.chrisrichardson.eventstore.examples.todolist.TodoRepository;
import net.chrisrichardson.eventstore.examples.todolist.common.model.ResourceWithUrl;
import net.chrisrichardson.eventstore.examples.todolist.hateoas.TodoUpdateService;
import net.chrisrichardson.eventstore.examples.todolist.model.Todo;

public class TodoViewServiceImpl implements TodoUpdateService {

	private RestTemplate restTemplate;

	@Value("${todoview.service.url}")
	private String todoviewServiceUrl;

	private TodoRepository repository;

	public String getTodoviewServiceUrl() {
		return todoviewServiceUrl;
	}

	public void setTodoviewServiceUrl(String todoviewServiceUrl) {
		this.todoviewServiceUrl = todoviewServiceUrl;
	}

	public TodoViewServiceImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public TodoViewServiceImpl(TodoRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<Todo> getAll() {
		// return repository.findAll();
		Assert.notNull(todoviewServiceUrl, "Should not be null: " + todoviewServiceUrl);
		List<Todo> result = new ArrayList<>();
		List<ResourceWithUrl<Todo>> cls = new ArrayList<ResourceWithUrl<Todo>>();
//		Type type = cls.getClass().getGenericSuperclass();
//		type.getClass();
		try {
			String uri = todoviewServiceUrl + "/todos";
			ResponseEntity<Todo[]> res =  restTemplate
					.getForEntity(uri, Todo[].class );
			
			Todo[] lst =  res.getBody();
			
			for (Todo todo : lst) {
				result.add(todo);
			}
			
		} catch (HttpClientErrorException e) {

		}
		return result;
	}

	@Override
	public CompletableFuture<Todo> findById(String todoId) {
		Todo res = repository.findOne(todoId);
		if (res != null) {
			return CompletableFuture.completedFuture(res);
		}
		return CompletableFutureUtil.failedFuture(new EntityNotFoundException("No todo found for given id"));
	}

}
