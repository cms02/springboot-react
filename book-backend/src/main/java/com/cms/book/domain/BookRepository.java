package com.cms.book.domain;

import org.springframework.data.jpa.repository.JpaRepository;

// @Repository 적어야 스프링 IoC에 빈으로 등록이 되는데..!!
// JpaRepository를 extends 하면 생략 가능함
// JpaRepository는 CRUD함수를 들고있음
public interface BookRepository extends JpaRepository<Book, Long> {

}
