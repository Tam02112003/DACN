package DACN.DACN.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Entity
public class Category {
    @Id
    @GeneratedValue
    private Long Id;

    @NotEmpty(message = "Tên thể loại là bắt buộc")
    private String name;
    //Construtor
    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }
    //Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }


}
