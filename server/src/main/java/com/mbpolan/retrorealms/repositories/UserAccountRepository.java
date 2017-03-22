package com.mbpolan.retrorealms.repositories;

import com.mbpolan.retrorealms.repositories.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository that provides access to user accounts.
 *
 * @author mbpolan
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    UserAccount findById(Long id);
}
