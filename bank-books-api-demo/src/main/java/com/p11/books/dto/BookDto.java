package com.p11.books.dto;

import com.p11.masking.annotation.Mask;
import com.p11.masking.core.MaskStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Book data transfer object")
public class BookDto {

    @Schema(description = "Unique identifier")
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    @Schema(description = "Book title")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 255)
    @Schema(description = "Author name")
    private String author;

    @Email(message = "Invalid email format")
    @Mask(style = MaskStyle.PARTIAL)
    @Schema(description = "Author contact email")
    private String email;

    @Mask(style = MaskStyle.PARTIAL)
    @Schema(description = "Author contact phone")
    private String phoneNumber;

    @Schema(description = "Publisher name")
    private String publisher;

    public BookDto() {}

    public BookDto(Long id, String title, String author,
                   String email, String phoneNumber, String publisher) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.publisher = publisher;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }


    @Override
    public String toString() {
        return "BookDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", email='" + MaskStyle.PARTIAL.apply(email, '*') + '\'' +
                ", phoneNumber='" + MaskStyle.PARTIAL.apply(phoneNumber, '*') + '\'' +
                ", publisher='" + publisher + '\'' +
                '}';
    }
}
