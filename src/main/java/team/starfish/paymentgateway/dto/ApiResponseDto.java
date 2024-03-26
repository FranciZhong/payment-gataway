package team.starfish.paymentgateway.dto;

import lombok.Data;

@Data
public class ApiResponseDto {
    private boolean success;
    private Object data;
    private String message;
}
