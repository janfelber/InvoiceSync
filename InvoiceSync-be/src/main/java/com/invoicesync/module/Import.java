package com.invoicesync.module;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
