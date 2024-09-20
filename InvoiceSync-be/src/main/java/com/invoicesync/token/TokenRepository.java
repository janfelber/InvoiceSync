package com.invoicesync.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {

    // get all the tokens from token table which are valid
    @Query("""
select t from Token t inner join UserDemo u on t.user.id = u.id where u.id = :userId and (t.expired = false or t.revoked = false)
""")
    List<Token> findAllValidTokensByUser(Long userId);


    Optional<Token> findByToken(String token);
}
