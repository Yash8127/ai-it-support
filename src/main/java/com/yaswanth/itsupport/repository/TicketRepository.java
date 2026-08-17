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
	List<Ticket> findByStatus(TicketStatus status);

	List<Ticket> findByPriority(TicketPriority priority);

	List<Ticket> findByStatusAndPriority(TicketStatus status, TicketPriority priority);

	@Query("""
			    SELECT t FROM Ticket t
			    WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
			       OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
			""")
	List<Ticket> searchTickets(@Param("keyword") String keyword);

}