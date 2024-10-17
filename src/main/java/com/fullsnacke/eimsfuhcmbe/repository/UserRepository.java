package com.fullsnacke.eimsfuhcmbe.repository;

import com.fullsnacke.eimsfuhcmbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByFuId(String fuId);

    User findByFuIdAndIsDeleted(String fuId, boolean isDeleted);

    User findByEmailAndIsDeleted(String email, boolean isDeleted);

    Optional<User> findByEmail(String email);

    User findUserByEmail(String email);

    User findUserById(int id);

    List<User> findAllByIsDeleted(boolean isDeleted);
}
