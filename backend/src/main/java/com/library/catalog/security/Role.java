package com.library.catalog.security;

public enum Role {
    ADMIN,
    LIBRARIAN,
    PATRON;

    public boolean canWriteCatalog() {
        return this == ADMIN || this == LIBRARIAN;
    }
}
