package org.systemexception.ecommuter.pojo;

public class UserDataSantizer {

    private UserDataSantizer() {
    }

    public static String returnAsSafe(final String userInputData) {

        return userInputData.replaceAll("[\n\r\t]", "_");
    }
}
