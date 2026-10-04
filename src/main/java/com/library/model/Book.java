package com.library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false, length = 255)
  private String title;

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false, length = 255)
  private String author;

  @NotBlank
  @Size(max = 100)
  @Column(nullable = false, length = 100)
  private String genre;

  @Pattern(regexp = "^(?:\\d{10}|\\d{13})$")
  @Size(max = 20)
  @Column(length = 20, unique = true)
  private String isbn;

  @Min(1450)
  @Max(2100)
  @Column(name = "published_year")
  private Integer publishedYear;

  @Min(1)
  @Max(10000)
  @Column
  private Integer pages;

  @Min(1)
  @ColumnDefault("1")
  @Column(nullable = false)
  @Builder.Default
  private Integer copies = 1;

  @Min(0)
  @ColumnDefault("1")
  @Column(name = "available_copies", nullable = false)
  @Builder.Default
  private Integer availableCopies = 1;
  
  @Column(nullable = false)
  @Builder.Default
  private Double mediaRating = 0.0;

  @Column(nullable = false)
  @Builder.Default
  private Integer totalRatings = 0;
}
