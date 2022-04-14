package pl.kurs.java.error;

import lombok.Value;

@Value
public class EntityNotFoundException extends RuntimeException {
    private String name;
    private String key;
}
