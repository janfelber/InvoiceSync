package com.invoicesync.token;

public interface TokenRepository { //JpaRepository<Token,Long> {

    // get all the tokens from token table which are valid
  //   @Query("""
  //       select t from Token t inner join UserDemo u on t.user.id = u.id where u.id = :userId and (t.expired = false or t.revoked = false)
  //       """)
  //   List<Token> findAllValidTokensByUser(Long userId);
  //
  // Optional<Token> findByToken(String token);
}
