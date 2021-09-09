package com.todo.restfulapi.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "task")
@Data // lombok
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;
    private int status;

    @ManyToOne (targetEntity = User.class)
    private User user;
}
