package dashboard.mapper;

import dashboard.dto.fe.NoteFeDto;
import dashboard.entity.NoteEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    NoteFeDto noteEntityToNoteFeDto(NoteEntity noteEntity);

    List<NoteFeDto> noteEntityListToNoteFeDtoList(List<NoteEntity> noteEntityList);

    NoteEntity noteFeDtoToNoteEntity(NoteFeDto noteFeDto);
}
