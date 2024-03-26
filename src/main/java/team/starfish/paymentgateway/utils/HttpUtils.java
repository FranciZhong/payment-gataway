package team.starfish.paymentgateway.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import team.starfish.paymentgateway.dto.ApiResponseDto;
import team.starfish.paymentgateway.error.BadRequestException;

import java.util.List;

public class HttpUtils {

    public static ResponseEntity<ApiResponseDto> respond(HttpStatus status,
                                                         boolean success,
                                                         String message) {
        ApiResponseDto response = new ApiResponseDto();
        response.setSuccess(success);
        response.setMessage(message);

        return ResponseEntity.status(status)
                .body(response);
    }

    public static ResponseEntity<ApiResponseDto> respond(HttpStatus status,
                                                         boolean success,
                                                         String message,
                                                         Object data) {
        ApiResponseDto response = new ApiResponseDto();
        response.setSuccess(success);
        response.setMessage(message);
        response.setData(data);

        return ResponseEntity.status(status)
                .body(response);
    }


    public static void fromValidResult(BindingResult bindingResult) throws BadRequestException {
        if (!bindingResult.hasErrors()) {
            return;
        }

        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        throw new BadRequestException("Errors: " + String.join("; ", errors));
    }
}
