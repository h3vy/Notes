package notes.entity;

import org.springframework.security.core.userdetails.User;

import javax.persistence.*;

@Entity
public class NoteEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "USER")
    private String user;

    @Column(name = "NOTENAME")
    private String name;

    @Column(name = "TEXT", length = 16383)
    private String text;

    public NoteEntity() { }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public NoteEntity(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}

