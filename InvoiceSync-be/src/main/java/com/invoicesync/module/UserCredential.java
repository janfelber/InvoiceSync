package com.invoicesync.module;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "\"USER_CREDENTIAL\"", schema = "invoice_sync")
public class UserCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"", nullable = false)
    private Long id;

    @NotNull
    @NotEmpty
    @NotNull
    @Column(name = "\"USERNAME\"", nullable = false)
    private String username;


    @Column(name = "\"PASSWORD\"", nullable = false)
    private String password;

    @Column(name = "\"LOGIN\"", nullable = false)
    private String login;

//  @OneToMany(mappedBy = "userCredential", fetch = FetchType.LAZY)
//  @JsonIgnoreProperties("userCredential")
//  private List<InvoiceImport> invoiceImports;

}