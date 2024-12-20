package com.example.blogs.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="post")
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;
    @Column(name="postTitle", length=100, nullable = false)
    private String title;
    @Column(length=100000)
    private String content;
    private String imageName;
    private Date addDate;
    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;
    @ManyToOne
    private User user;
    @OneToMany(mappedBy="post", cascade = CascadeType.ALL)
    private Set<Comment> comments= new HashSet<>();

    public void setId(int i) {
    }
}
