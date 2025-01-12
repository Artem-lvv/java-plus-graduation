package ru.yandex.practicum.event.model;

import ru.yandex.practicum.category.model.Category;
import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.location.model.Location;
import ru.yandex.practicum.state.State;
import ru.yandex.practicum.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 2000)
    private String annotation;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(nullable = false)
    private long category;

    @Column(nullable = false)
    private int confirmedRequests;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false, length = 7000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDate;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(nullable = false)
    @ToString.Exclude
    private long initiator;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(nullable = false)
    @ToString.Exclude
    private long location;

    @Column(nullable = false)
    private boolean paid;

    @Column(nullable = false)
    private int participantLimit;

    @Column
    private LocalDateTime publishedOn;

    @Column(nullable = false)
    private boolean requestModeration;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false)
    private long views;
}
