package com.upstage.devup.user.account.repository;

import com.upstage.devup.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);
}
