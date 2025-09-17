package dashboard.service;

import dashboard.dto.fe.NoteFeDto;
import dashboard.entity.NoteEntity;
import dashboard.mapper.NoteMapper;
import dashboard.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    @Cacheable("notesCache")
    public List<NoteFeDto> getAllNotes() {
        return noteMapper.noteEntityListToNoteFeDtoList(noteRepository.findAll());
    }

    @CacheEvict(value = "notesCache", allEntries = true)
    public NoteFeDto createNewNote(NoteFeDto note) {

        NoteEntity newNoteEntity = noteMapper.noteFeDtoToNoteEntity(note);

        noteRepository.save(newNoteEntity);

        return noteMapper.noteEntityToNoteFeDto(newNoteEntity);
    }

    @CacheEvict(value = "notesCache", allEntries = true)
    public void deleteNote(Long id) {
        NoteEntity noteEntity = findNoteByIdOrThrow(id);
        noteRepository.delete(noteEntity);
    }

    @CacheEvict(value = "notesCache", allEntries = true)
    public NoteFeDto updateNote(NoteFeDto note) {
        NoteEntity noteEntity = findNoteByIdOrThrow(note.id());
        if (note.text() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Text can't be null");
        }

        noteEntity.setText(note.text());
        noteRepository.save(noteEntity);

        return noteMapper.noteEntityToNoteFeDto(noteEntity);
    }

    private NoteEntity findNoteByIdOrThrow(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id can't be null");
        }
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note with id: " + id + " doesn't exist"));
    }
}
