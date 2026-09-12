package com.quizbotv2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "marks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Marks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "quizzes_id", nullable = false)
    private Quizzes quizzes;

    @Column(nullable = false)
    private int mark;

    @Column(nullable = false)
    private String totalTime;

    @Column(nullable = false)
    private String questionCount;

}
