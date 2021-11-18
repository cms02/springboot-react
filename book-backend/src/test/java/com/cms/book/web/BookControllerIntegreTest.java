package com.cms.book.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.cms.book.domain.Book;
import com.cms.book.domain.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;



/**
 * //통합 테스트(모든 Bean들을 똑같이 IoC 올리고 테스트 하는 것)
 * WebEnvironment.MOCK = 실제 톰캣을 올리는게 아니라, 다른 톰캣으로 테스트
 * WebEnvironment.RANDOM_POR = 실제 톰캣으로 테스트
 * @AutoConfigureMockMvc MockMvc를 IoC에 등록해줌.
 * @Transactional 각각의 테스트함수가 종료될 때마다 트랜잭션을 rollback해주는 어노테이션!
 * 
 *
 */

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK) // 실제 톰캣을 올리는게 아니라, 다른 톰캣으로 테스트
public class BookControllerIntegreTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@BeforeEach //메소드 실행 전 increment 초기화
	public void init() {
//		entityManager.createNativeQuery("ALTER TABLE book AUTO_INCREMENT =  1").executeUpdate(); //mysql
		entityManager.createNativeQuery("ALTER TABLE book ALTER COLUMN id RESTART WITH 1").executeUpdate(); //h2
	}
	
	//BDDMockito 패턴 given, when, then
	@Test
	public void save_테스트() throws Exception{
		// given (테스트를 하기 위한 준비)
		Book book = new Book(null, "스프링 따라하기","cms");
		String content = new ObjectMapper().writeValueAsString(book);
//		when(bookService.저장하기(book)).thenReturn(new Book(1L,"스프링 따라하기","cms")); 필요없음 실제 service가 수행
		
		//when (테스트 실행)
		ResultActions resultAction = mockMvc.perform(post("/book")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(content)
						.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then (검증)
		resultAction
			.andExpect(status().isCreated()) //201
			.andExpect(jsonPath("$.title").value("스프링 따라하기"))
			.andDo(MockMvcResultHandlers.print());
		
	}
	
	@Test
	public void findAll_테스트() throws Exception{
		//given
		List<Book> books = new ArrayList<>();
		books.add(new Book(null,"스프링부트 따라하기","cms"));
		books.add(new Book(null,"리엑트 따라하기","cms"));
		books.add(new Book(null,"JUnit 따라하기","cms"));
//		when(bookService.모두가져오기()).thenReturn(books);
		bookRepository.saveAll(books);
		
		
		//when
		ResultActions resultAction = mockMvc.perform(get("/book")
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then
		resultAction
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[0].id").value(1L))
			.andExpect(jsonPath("$", Matchers.hasSize(3)))
			.andExpect(jsonPath("$.[2].title").value("JUnit 따라하기"))
			.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void findById_테스트() throws Exception{
		//given
		Long id = 2L;
		
		List<Book> books = new ArrayList<>();
		books.add(new Book(null,"스프링부트 따라하기","cms"));
		books.add(new Book(null,"리엑트 따라하기","cms"));
		books.add(new Book(null,"JUnit 따라하기","cms"));
		bookRepository.saveAll(books);
		
//		when(bookService.한건가져오기(id)).thenReturn(new Book(1L,"자바 공부하기","쌀"));
		
		// when
		ResultActions resultAction = mockMvc.perform(get("/book/{id}",id)
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then
		resultAction
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value("리엑트 따라하기"))
			.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void update_테스트() throws Exception{
		//given
		Long id = 3L;
		
		List<Book> books = new ArrayList<>();
		books.add(new Book(null,"스프링부트 따라하기","cms"));
		books.add(new Book(null,"리엑트 따라하기","cms"));
		books.add(new Book(null,"JUnit 따라하기","cms"));
		bookRepository.saveAll(books);
		
		Book book = new Book(null, "C++ 따라하기","cms");
		String content = new ObjectMapper().writeValueAsString(book);
//		when(bookService.수정하기(id,book)).thenReturn(new Book(1L,"C++ 따라하기","cms"));
		
		// when
		ResultActions resultAction = mockMvc.perform(put("/book/{id}",id)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then
		resultAction
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(3L))
			.andExpect(jsonPath("$.title").value("C++ 따라하기"))
			.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void delete_테스트() throws Exception{
		//given
		Long id = 1L;
		
		List<Book> books = new ArrayList<>();
		books.add(new Book(null,"스프링부트 따라하기","cms"));
		bookRepository.saveAll(books);
		
		// when
		ResultActions resultAction = mockMvc.perform(delete("/book/{id}",id)
				.accept(MediaType.TEXT_PLAIN));
		
		//then
		resultAction
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print());
		
		MvcResult requestResult = resultAction.andReturn();
		String result = requestResult.getResponse().getContentAsString();
		
		assertEquals("ok", result);
	}
	
	
	
}
