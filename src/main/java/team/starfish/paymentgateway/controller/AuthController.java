package team.starfish.paymentgateway.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.starfish.paymentgateway.dto.ApiResponseDto;
import team.starfish.paymentgateway.dto.auth.AuthResultDto;
import team.starfish.paymentgateway.dto.auth.WalletDto;
import team.starfish.paymentgateway.dto.auth.WalletRawDto;
import team.starfish.paymentgateway.error.BadRequestException;
import team.starfish.paymentgateway.error.DuplicatedDataException;
import team.starfish.paymentgateway.service.JwtService;
import team.starfish.paymentgateway.service.WalletService;
import team.starfish.paymentgateway.utils.HttpUtils;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private WalletService walletService;

    @Resource
    private JwtService jwtService;


    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> registerWallet(@Valid @RequestBody WalletRawDto body,
                                                         BindingResult bindingResult)
            throws BadRequestException, DuplicatedDataException {
        HttpUtils.fromValidResult(bindingResult);

        walletService.registerWallet(body);
        return HttpUtils.respond(HttpStatus.CREATED, true, "Successfully register your new wallet.");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> loginWallet(@Valid @RequestBody WalletRawDto body,
                                                      BindingResult bindingResult)
            throws Exception {
        HttpUtils.fromValidResult(bindingResult);

        Optional<WalletDto> walletDtoOpt = walletService.authByPassword(body);
        if (walletDtoOpt.isEmpty()) {
            return HttpUtils.respond(HttpStatus.UNAUTHORIZED, false, "Invalid email or password.");
        }

        AuthResultDto authResultDto = new AuthResultDto();
        authResultDto.setToken(jwtService.generateJwtToken(walletDtoOpt.get()));
        return HttpUtils.respond(HttpStatus.OK, true, "Successfully login.", authResultDto);
    }
}
