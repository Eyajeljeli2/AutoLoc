package tn.esprit.autoloc.util;

import jakarta.persistence.Id;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public final class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    public static void copyNonNull(Object source, Object target) {
        BeanWrapper src = new BeanWrapperImpl(source);
        BeanWrapper tgt = new BeanWrapperImpl(target);
        String idName = idFieldName(target.getClass());
        for (PropertyDescriptor pd : src.getPropertyDescriptors()) {
            String name = pd.getName();
            if ("class".equals(name) || name.equals(idName)) {
                continue;
            }
            if (!src.isReadableProperty(name) || !tgt.isWritableProperty(name)) {
                continue;
            }
            Object value = src.getPropertyValue(name);
            if (value != null) {
                tgt.setPropertyValue(name, value);
            }
        }
    }

    private static String idFieldName(Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field.getName();
            }
        }
        return null;
    }
}