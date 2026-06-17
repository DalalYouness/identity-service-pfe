package com.dalal.identityservicepfe.entities;

import com.dalal.identityservicepfe.enums.AccountStatus;
import com.dalal.identityservicepfe.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    @Column(name = "full_name")
    private String fullName;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AuthProvider authProvider = AuthProvider.LOCAL;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.ACTIVE;
    @Column(name = "created_at")
    private LocalDateTime creationAt;
    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    @ManyToMany(fetch = FetchType.EAGER) // i kept it eager because we don't have  a huge data to return just max three roles
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    //that's what happen when we use lazy fetch a proxy who implement the interface collections to
    //thanks to him, we save our memory from unused data
    //private Set<Role> roles = new PersistentSet<>();

    @PrePersist
    public void onCreate() {
        this.creationAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
    @PreUpdate
    public void onUpdate() {
        this.updateAt = LocalDateTime.now();
    }


    // from UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName().name())).toList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountStatus != AccountStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.accountStatus == AccountStatus.ACTIVE;
    }
}
//done Alhamdulilah 👌
