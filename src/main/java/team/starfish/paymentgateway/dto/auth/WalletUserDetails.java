package team.starfish.paymentgateway.dto.auth;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import team.starfish.paymentgateway.entity.Wallet;

import java.util.Collection;
import java.util.Collections;

public class WalletUserDetails implements UserDetails {

    private final Wallet wallet;

    public WalletUserDetails(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    public Long getWalletId() {
        return wallet.getId();
    }

    public WalletDto toDto() {
        return new ModelMapper().map(this.wallet, WalletDto.class);
    }

    @Override
    public String getPassword() {
        return wallet.getCredential();
    }

    @Override
    public String getUsername() {
        return wallet.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return wallet.getValid();
    }
}