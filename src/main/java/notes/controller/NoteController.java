package notes.controller;

import notes.entity.NoteEntity;
import notes.entity.SearchEntity;
import notes.entity.UserEntity;
import notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.ibm.icu.text.Transliterator;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/notes")
public class NoteController {
    private long GlobalId = 0;
    private final NoteRepository library;
    UserEntity userEntity = new UserEntity();
    public Boolean beCyrillic = false;
    public static final String CYRILLIC_TO_LATIN = "Cyrillic-Latin"; //
    public static final String LATIN_TO_CYRILLIC = "Latin-Cyrillic";
    Transliterator toLatinTrans = Transliterator.getInstance(CYRILLIC_TO_LATIN); //
    Transliterator toCyrillicTrans = Transliterator.getInstance(LATIN_TO_CYRILLIC);

    @Autowired
    public NoteController(NoteRepository library) {
        this.library = library;
    }

    @GetMapping("")
    public String index (@ModelAttribute("SearchEntity") SearchEntity searchEntity, Model model, Principal principal){
        Iterable<NoteEntity> notes = library.findAllByUser(principal.getName());
        userEntity.setUsername(principal.getName());
        model.addAttribute("notes", notes);
        model.addAttribute("SearchEntity", new SearchEntity());
        model.addAttribute("UserEntity", userEntity);
        return "notes/index";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("SearchEntity") SearchEntity searchEntity, Model model){
        model.addAttribute("NoteEntity", new NoteEntity());
        model.addAttribute("SearchEntity", new SearchEntity());
        model.addAttribute("UserEntity", userEntity);
        return "notes/new";
    }

    @PostMapping("")
    public String create(@ModelAttribute("NoteEntity") NoteEntity noteEntity, Principal principal) {
        NoteEntity s = new NoteEntity(noteEntity.getName(), noteEntity.getText());
        s.setUser(principal.getName());
        library.save(s);
        return "redirect:/notes";
    }

    @GetMapping("/{id}")
    public String show(@ModelAttribute("SearchEntity") SearchEntity searchEntity, @PathVariable("id") long id, Model model, Principal principal){
//        Показываем одну заметку
        Optional<NoteEntity> noteEntity = library.findById(id);
        model.addAttribute("NoteEntity",noteEntity.get());
//        Список который показываем слева
        Iterable<NoteEntity> notes = library.findAllByUser(principal.getName());
        model.addAttribute("notes", notes);
//        Поиск
        model.addAttribute("SearchEntity", new SearchEntity());
//        User
        model.addAttribute("UserEntity", userEntity);
        return "notes/show";
    }


    @PostMapping("/search")
    public String sendSearch(@ModelAttribute("SearchEntity") SearchEntity searchEntity, Model model){
        model.addAttribute("SearchEntity", searchEntity);
        String result = "";

        if (searchEntity.getTextNote().equals("")){
            return "redirect:/notes/errorSearch";
        }

        for(int i = 0; i < searchEntity.getTextNote().length(); i++) {
            if(Character.UnicodeBlock.of(searchEntity.getTextNote().charAt(i)).equals(Character.UnicodeBlock.CYRILLIC)) {
                result = toLatinTrans.transliterate(searchEntity.getTextNote());
                beCyrillic = true;
            }
            else {
                result = searchEntity.getTextNote();
                beCyrillic = false;
            }
        }

        System.out.println( "/notes/search/" + result);
        return "redirect:/notes/search/" + result;
    }

    @GetMapping( "/search/{searchName}")
    public String showSearch(@ModelAttribute("SearchEntity") SearchEntity searchEntity, @PathVariable("searchName") String searchName, Model model, Principal principal){
//        Список который показываем слева
        Iterable<NoteEntity> notes = library.findAllByUser(principal.getName());
        model.addAttribute("notes", notes);
        model.addAttribute("UserEntity", userEntity);
//        Проверка является ли слово русским
        if (beCyrillic) {
            searchName = toCyrillicTrans.transliterate(searchName);
        }
//        Список найденных по имени
        List<NoteEntity> searchNoteEntity = library.findAllByTextContains(searchName);
        List<NoteEntity> showSearchNoteEntity = new ArrayList<NoteEntity>();
        for(NoteEntity note : searchNoteEntity ){
            if (principal.getName().equals(note.getUser())){
                showSearchNoteEntity.add(note);    
            }
        }
//        Поиск
        model.addAttribute("SearchEntity", new SearchEntity());
        if (showSearchNoteEntity.isEmpty()) {
            return "notes/errorSearch";
        }
        model.addAttribute("searchNoteEntity",showSearchNoteEntity);
        return "notes/search";
    }

    @GetMapping("/{id}/edit")
    public String edit(@ModelAttribute("SearchEntity") SearchEntity searchEntity, Model model, @PathVariable("id") long id, Principal principal){
//        Показываем одну заметку
        Optional<NoteEntity> noteEntity = library.findById(id);
        model.addAttribute("NoteEntity",noteEntity.get());
//        Список который показываем слева
        Iterable<NoteEntity> notes = library.findAllByUser(principal.getName());
        model.addAttribute("notes", notes);
//        Поиск
        model.addAttribute("SearchEntity", new SearchEntity());
        GlobalId = id;
//        User
        model.addAttribute("UserEntity", userEntity);
        return "notes/edit";
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute("NoteEntity") NoteEntity noteEntity, Principal principal) {
        Optional<NoteEntity> s = library.findById(GlobalId);
        s.get().setId(GlobalId);
        s.get().setText(noteEntity.getText());
        s.get().setName(noteEntity.getName());
        s.get().setUser(principal.getName());
        library.save(s.get());
        return "redirect:/notes";
    }

    @PostMapping("/{id}")
    public String delete(@PathVariable("id") long id){
        library.deleteById(id);
        return "redirect:/notes";
    }

    @GetMapping("/errorSearch")
    public String error( Model model, Principal principal){
        Iterable<NoteEntity> notes = library.findAllByUser(principal.getName());
        model.addAttribute("notes", notes);
        model.addAttribute("UserEntity", userEntity);
        model.addAttribute("SearchEntity", new SearchEntity());
        return "notes/errorSearch";
    }

}
