package com.appsdeveloperblog.tutorials.junit.io;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends PagingAndSortingRepository<UserEntity, Long>, JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
}
