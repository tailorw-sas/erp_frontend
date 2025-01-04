package com.kynsof.audit.infrastructure.core;

import java.util.concurrent.atomic.AtomicReference;

public class UserContext {
    private static AtomicReference<String> currentUser= new AtomicReference<>();


    public static void setCurrentUser(String username){
        currentUser.set(username);
    }

    public static String getCurrentUser(){
        return currentUser.get();
    }

}
