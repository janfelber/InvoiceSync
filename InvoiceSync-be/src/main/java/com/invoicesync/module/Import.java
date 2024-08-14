package com.invoicesync.module;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "import_file", schema = "invoice_sync")
public class Import {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    @ManyToOne()
    @JoinColumn(name = "credential_id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "company_id")
    private Company company;

    @NotEmpty
    @Column(name = "filename")
    private String filename;

    @NotEmpty
    @Column(name = "created_at")
    private String createdAt;

    public @NotEmpty String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(@NotEmpty String xmlContent) {
        this.xmlContent = xmlContent;
    }

    @NotEmpty
    @Column(name = "xml_content")
    private String xmlContent;

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public @NotEmpty String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NotEmpty String createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
