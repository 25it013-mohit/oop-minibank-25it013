package service;

import model.annotation.Id;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class Repository<T> {

    private final HashMap<String, T> data = new HashMap<>();

    public T save(T entity) {
        try {
            Class<?> current = entity.getClass();

            while (current != null) {
                for (Field field : current.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Id.class)) {
                        field.setAccessible(true);

                        String id = String.valueOf(field.get(entity));
                        data.put(id, entity);

                        return entity;
                    }
                }

                current = current.getSuperclass();
            }

            throw new IllegalArgumentException(
                    "No @Id field found in " + entity.getClass().getName()
            );

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to access @Id field", e);
        }
    }

    public T findById(String id) {
        return data.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(data.values());
    }

    public boolean delete(String id) {
        return data.remove(id) != null;
    }

    @SafeVarargs
    public final void saveAll(T... items) {
        for (T item : items) {
            save(item);
        }
    }

    public static <N extends Number> double average(N[] values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }

        double sum = 0;

        for (N value : values) {
            sum += value.doubleValue();
        }

        return sum / values.length;
    }
}