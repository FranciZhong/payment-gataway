package team.starfish.paymentgateway.service;

import jakarta.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.starfish.paymentgateway.dto.auth.WalletDto;
import team.starfish.paymentgateway.dto.auth.WalletRawDto;
import team.starfish.paymentgateway.dto.auth.WalletUserDetails;
import team.starfish.paymentgateway.entity.Wallet;
import team.starfish.paymentgateway.error.DuplicatedDataException;
import team.starfish.paymentgateway.repository.WalletRepository;

import java.util.Optional;

@Service
public class WalletService implements UserDetailsService {

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private WalletRepository walletRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registerWallet(WalletRawDto walletRawDto) throws DuplicatedDataException {
        Optional<Wallet> walletOpt = walletRepository.findByEmail(walletRawDto.getEmail(), true);
        if (walletOpt.isPresent()) {
            throw new DuplicatedDataException("Email is already registered.");
        }
        Wallet wallet = new Wallet();
        wallet.setEmail(walletRawDto.getEmail());
        wallet.setCredential(passwordEncoder.encode(walletRawDto.getPassword()));
        walletRepository.save(wallet);
    }

    public Optional<WalletDto> authByPassword(WalletRawDto walletRawDto) {
        return walletRepository.findByEmail(walletRawDto.getEmail(), true)
                .filter(wallet -> passwordEncoder.matches(walletRawDto.getPassword(), wallet.getCredential()))
                .map(wallet -> modelMapper.map(wallet, WalletDto.class));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Wallet wallet = walletRepository.findByEmail(username, true)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return new WalletUserDetails(wallet);
    }

}
