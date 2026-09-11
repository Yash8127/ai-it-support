package com.yaswanth.itsupport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yaswanth.itsupport.entity.Ticket;
import com.yaswanth.itsupport.enums.TicketPriority;
import com.yaswanth.itsupport.enums.TicketStatus;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

	List<Ticket> findByPriority(TicketPriority priority);

	List<Ticket> findByPriorityAndUserUsername(TicketPriority priority, String username);

	List<Ticket> findByStatus(TicketStatus status);

	List<Ticket> findByStatusAndUserUsername(TicketStatus status, String username);

	List<Ticket> findByStatusAndPriority(TicketStatus status, TicketPriority priority);

	List<Ticket> findByStatusAndPriorityAndUserUsername(TicketStatus status, TicketPriority priority, String username);

	List<Ticket> findByUserUsername(String username);

	List<Ticket> findByUserId(Long userId);

	@Query("""
			    SELECT t FROM Ticket t
			    WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
			       OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
			""")
	List<Ticket> searchTickets(@Param("keyword") String keyword);

	@Query("""
			SELECT t FROM Ticket t
			WHERE t.user.username = :username
			  AND (
			      LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
			      OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
			  )
			""")
	List<Ticket> searchTicketsByUser(@Param("keyword") String keyword, @Param("username") String username);

}