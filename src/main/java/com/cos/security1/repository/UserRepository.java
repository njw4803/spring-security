package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

//CRUD 함수를 JpaRepository가 들고있음
// @Repository라는 어노테이션이 없어도 IoC된다. 이유는 JpaRepository를 상속했기 때문에...
public interface UserRepository extends JpaRepository<User,Integer> {

    //findBy 규칙 => 뒤에 username이 where문에 조건으로 들어간다.
    // select * from suer where username = ?
    public User findByUsername(String username); //Jap Query methods

    // select * form user where email = ?
    public User findByEmail(String email);
}
