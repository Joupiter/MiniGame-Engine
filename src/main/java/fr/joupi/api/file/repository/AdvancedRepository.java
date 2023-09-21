package fr.joupi.api.file.repository;

import java.util.Collection;

public interface AdvancedRepository<ID, T extends Identifiable<ID>> extends Repository<ID, T> {

    T save(T object);

    void saveAll(Collection<T> objects);

    T find(ID identifier);

    Collection<T> findAll();

    boolean delete(T object);

    boolean delete(ID identifier);

}