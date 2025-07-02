package dashboard.dto;

import jakarta.validation.constraints.NotNull;

public record NoteFeDto(
        Long id,

        @NotNull
        String text
) {
}
