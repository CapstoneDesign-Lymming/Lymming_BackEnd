package com.supernova.lymming.kakao.jwt;

import com.supernova.lymming.kakao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String keyCode) throws UsernameNotFoundException {
        return userRepository.findByKeyCode(keyCode)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
    }
}
