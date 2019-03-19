package com.example.myapplication;

class AppState {
    private static final AppState ourInstance = new AppState();

    static AppState getInstance() {
        return ourInstance;
    }

    private AppState() {
    }
}
