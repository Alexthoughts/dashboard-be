package dashboard.service;

import dashboard.dto.NoteFeDto;
import dashboard.entity.NoteEntity;
import dashboard.mapper.NoteMapper;
import dashboard.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    public List<NoteFeDto> getAllNotes() {
        return noteMapper.noteEntityListToNoteFeDtoList(noteRepository.findAll());
    }

    public NoteFeDto createNewNote(NoteFeDto note) {

        NoteEntity newNoteEntity = noteMapper.noteFeDtoToNoteEntity(note);

        noteRepository.save(newNoteEntity);

        return noteMapper.noteEntityToNoteFeDto(newNoteEntity);
    }

    public void deleteNote(Long id) {
        NoteEntity noteEntity = findNoteByIdOrThrow(id);
        noteRepository.delete(noteEntity);
    }

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
