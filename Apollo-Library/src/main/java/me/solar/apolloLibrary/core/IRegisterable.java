package me.solar.apolloLibrary.core;

public interface IRegisterable {

    void register();
    void unregister();
    void reload();
    boolean isRegistered();

}
