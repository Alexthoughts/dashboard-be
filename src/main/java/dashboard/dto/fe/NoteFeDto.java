package dashboard.dto.fe;

import jakarta.validation.constraints.NotNull;

public record NoteFeDto(
        Long id,

        @NotNull
        String text
) {
}
