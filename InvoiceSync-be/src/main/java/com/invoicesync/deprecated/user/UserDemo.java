package com.invoicesync.deprecated.user;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.invoicesync.chartaccount.ChartAccount;
import com.invoicesync.company.Company;
import com.invoicesync.shared.token.Token;
import com.invoicesync.xmlFile.XmlFile;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// @Entity
// @Table(name = "user_credential", schema = "invoice_sync")
// @EntityListeners(AuditingEntityListener.class)
public class UserDemo implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String login;

    private boolean mfa_enabled;

    private String secret;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Token> tokens;

    @OneToMany(mappedBy = "user")
    private List<XmlFile> xmlFiles;

    @OneToMany(mappedBy = "user")
    private List<Company> companies;

    @OneToMany(mappedBy = "user")
    private List<ChartAccount> chartAccounts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getUserAuthorities();
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
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
    public String getName() {
        return username;
    }

}
