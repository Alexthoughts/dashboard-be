package dashboard.controller;

import dashboard.dto.fe.NoteFeDto;
import dashboard.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @GetMapping("/get-notes")
    public List<NoteFeDto> getNotesList() {
        return noteService.getAllNotes();
    }

    @PostMapping("/create-note")
    public ResponseEntity<NoteFeDto> createNote(@Valid @RequestBody NoteFeDto note) {
        NoteFeDto createdNote = noteService.createNewNote(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }

    @DeleteMapping("/delete-note/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable long id) {
        noteService.deleteNote(id);
        return ResponseEntity.ok("Note deleted successfully");
    }

    @PatchMapping("/update-note")
    public ResponseEntity<NoteFeDto> updateNote(@RequestBody NoteFeDto note) {
        NoteFeDto updatedNote = noteService.updateNote(note);
        return ResponseEntity.ok(updatedNote);
    }
}
