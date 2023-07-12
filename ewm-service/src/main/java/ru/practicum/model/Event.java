package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String annotation;
    private String description;

    @OneToOne(targetEntity = Category.class, fetch = FetchType.EAGER)
    private Category category;

    private LocalDateTime eventDate;
    private Float lat;
    private Float lon;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User initiator;

    @Enumerated(EnumType.STRING)
    private EventState state;
}
