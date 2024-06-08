package com.kynsof.share.core.application;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserSecurity implements UserDetails {

    private final String identity;

    private final String role;

    private final List<ProfileSecurity> profiles;

    private final Set<String> citizenAccess;

    private final String password;

    public UserSecurity(String identity, String role, List<ProfileSecurity> profiles, Set<String> citizenAccess) {
       this(identity, null, role, profiles, citizenAccess);
    }

    public UserSecurity(String identity, String password, String role, List<ProfileSecurity> profiles, Set<String> citizenAccess) {
        this.identity =  Objects.requireNonNull(identity, "The token identity can't be null");
        this.role = Objects.requireNonNull(role, "The role can't be null");
        this.profiles = Objects.requireNonNull(profiles, "The profile list can't be null");
        this.password = password;
        this.citizenAccess = citizenAccess;
       // this.citizenAccess = Objects.requireNonNull(citizenAccess, "The citizen access list can't be null");
    }

    public String getIdentity() {
        return identity;
    }

    public String getRole() {
        return role;
    }

    public List<ProfileSecurity> getProfiles() {
        return profiles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return identity;
    }

    public Set<String> getCitizenAccess() {
        return citizenAccess;
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
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = getAccessStream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    public Set<Long> getAgenciesByAccess(String accessCode) {
        return profiles.stream()
                .filter(p -> p.access().stream().anyMatch(a -> a.equals(accessCode)))
                .map(ProfileSecurity::agencyCode)
                .collect(Collectors.toSet());
    }

    public Set<Long> getAgenciesByAccess(String... accessCode) {
        Objects.requireNonNull(accessCode);
        return Arrays.stream(accessCode)
                .flatMap(code -> getAgenciesByAccess(code).stream())
                .collect(Collectors.toSet());
    }

    public Stream<String> getAccessStream() {
        Stream<String> profilesAccessStream = profiles.stream().flatMap(p -> p.access().stream());
        Stream<String> citizenAccessStream = citizenAccess != null ? citizenAccess.stream() :  Stream.of("Basic");
        return Stream.concat(profilesAccessStream, citizenAccessStream);
    }

    public boolean hasAccess(String access) {
        return getAccessStream().anyMatch(accessItem -> Objects.equals(accessItem, access));
    }

}
