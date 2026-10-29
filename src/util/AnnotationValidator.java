
package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import model.annotation.Id;
import model.annotation.Positive;
import model.annotation.MaxLength;

public class AnnotationValidator {

    public static List<String> validate(Object obj) {
        List<String> errors = new ArrayList<>();

        // Check fields in the class and its parent classes
        for (Class<?> type = obj.getClass();
             type != null;
             type = type.getSuperclass()) {

            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);

                try {
                    Object value = field.get(obj);

                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive annotation =
                                field.getAnnotation(Positive.class);

                        if (value instanceof Number
                                && ((Number) value).longValue() <= 0) {
                            errors.add(field.getName() + " "
                                    + annotation.message());
                        }
                    }

                    if (field.isAnnotationPresent(MaxLength.class)
                            && value instanceof String) {
                        MaxLength annotation =
                                field.getAnnotation(MaxLength.class);

                        if (((String) value).length()
                                > annotation.value()) {
                            errors.add(field.getName()
                                    + " exceeds maximum length "
                                    + annotation.value());
                        }
                    }

                    if (field.isAnnotationPresent(Id.class)
                            && (value == null
                            || value.toString().isBlank())) {
                        errors.add(field.getName()
                                + " must not be blank");
                    }

                } catch (IllegalAccessException e) {
                    errors.add("Cannot access field: "
                            + field.getName());
                }
            }
        }

        return errors;
    }
}
