package team.starfish.paymentgateway.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import team.starfish.paymentgateway.dto.auth.JwtValidResultDto;
import team.starfish.paymentgateway.service.JwtService;
import team.starfish.paymentgateway.service.WalletService;

import java.io.IOException;
import java.util.Optional;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final WalletService walletService;

    public JwtAuthFilter(JwtService jwtService, WalletService walletService) {
        this.jwtService = jwtService;
        this.walletService = walletService;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getServletPath().contains("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> tokenOpt = extractTokenFromRequest(request);
        if (tokenOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = tokenOpt.get();
        JwtValidResultDto jwtValidResultDto = jwtService.validateJwtToken(token);
        if (!jwtValidResultDto.isValid()) {
            filterChain.doFilter(request, response);
            return;
        }

        // authorization to context
        UserDetails userDetails = walletService
                .loadUserByUsername(jwtValidResultDto.getWallet().getEmail());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        return Optional.ofNullable(bearerToken)
                .filter(StringUtils::isNoneEmpty)
                .filter(bt -> bt.startsWith("Bearer "))
                .map(bt -> bt.substring(7));
    }
}