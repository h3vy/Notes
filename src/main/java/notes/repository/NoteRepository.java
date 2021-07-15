package notes.repository;

import notes.entity.NoteEntity;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface NoteRepository extends CrudRepository<NoteEntity, Long> {

    List<NoteEntity> findByName(String name);

    Iterable<NoteEntity> findAllByUser(String user);

    List<NoteEntity> findAllByTextContains(String text);

}
