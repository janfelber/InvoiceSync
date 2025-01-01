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
@Table(name="script_schema_mapping", schema = "invoice_sync")
public class ScriptSchema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "credential_id")
    private User user;

    @NotEmpty
    @Column(name = "script")
    private String script;

    @NotEmpty
    @Column(name = "schema")
    private String schema;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String scriptName) {
        this.script = scriptName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schemaName) {
        this.schema = schemaName;
    }
}
