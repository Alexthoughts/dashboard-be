package dashboard.dto;

import java.util.List;

public record ConvertResponseDto(
        ConvertedResultResponseDto result,
        Boolean success,
        List<String> validationMessage
) {
}
