package com.invoicesync.qrlogin;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QRLoginRepository extends JpaRepository<QRLogin, UUID> {

  Optional<QRLogin> findByToken(String token);

}
