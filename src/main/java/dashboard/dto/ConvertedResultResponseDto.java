package dashboard.dto;

public record ConvertedResultResponseDto(
        Double convertedAmount,
        String from,
        String to
) {
}
