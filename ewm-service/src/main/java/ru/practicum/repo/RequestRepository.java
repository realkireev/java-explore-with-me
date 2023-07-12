package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;

import java.util.List;


public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    Integer countAllByEventIdAndStatus(Long eventId, RequestStatus status);

    Integer countAllByStatusAndIdIn(RequestStatus status, List<Long> ids);

    Integer countAllByEventId(Long eventId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Request r SET r.status = :status WHERE r.id IN :ids")
    void updateRequestStatus(@Param("status") RequestStatus status, @Param("ids") List<Long> ids);
}
