package pl.kurs.java.error;

import lombok.Value;

@Value
public class InvalidDateException extends RuntimeException {

    private String name;
    private String key;


    public InvalidDateException(String name, String key) {
        this.name = name;
        this.key = key;
    }
}
