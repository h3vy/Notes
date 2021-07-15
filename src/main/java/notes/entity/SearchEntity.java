package notes.entity;

public class SearchEntity {

    private String textNote;

    public SearchEntity() { }

    public SearchEntity(String textNote) {
        this.textNote = textNote;
    }

    public String getTextNote() {
        return textNote;
    }

    public void setTextNote(String textNote) {
        this.textNote = textNote;
    }
}
