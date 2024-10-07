package com.cos.security.repository;

import com.cos.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


//CRUD 함수를 JpaRepositoty가 들고 있음
@Repository //없어도 IoC가 된다 -> JpaRepository를 상속했기 때문에 자동으로 Bean 등록됨
public interface UserRepository extends JpaRepository<User, Integer> {
    /*
    Jpa Query Methods 검색해보면 레퍼런스 나옴
    findBy는 규칙 -> Username은 문법
    select * from user where username = 매개변수
     */
    User findByUsername(String username);
}
