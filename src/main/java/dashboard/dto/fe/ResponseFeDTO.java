package dashboard.dto.fe;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFeDTO<T> {

    @Nullable
    private T data;

    @Nullable
    private List<String> errors;
}
