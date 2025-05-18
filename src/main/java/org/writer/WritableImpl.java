package org.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Реализация интерфейса Writable, предоставляющая функциональность для записи данных в CSV-файл.
 */
public class WritableImpl implements Writable {

    /**
     * Записывает список объектов в CSV-файл. Если список пуст или равен null, метод завершает выполнение без записи.
     *
     * @param data     Список объектов, которые необходимо записать в файл.
     * @param fileName Имя файла (без расширения), в который будут записаны данные.
     * @throws RuntimeException Возникает если ошибка доступа к полям объекта или ошибка записи в файл.
     */
    public void writeToFile(List<?> data, String fileName) {
        if (validData(data, fileName)) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        // Получение имен полей
        Class<?> clazz = data.get(0).getClass();

        String fieldNames = Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.joining(","));

        builder.append(fieldNames).append("\n");

        // Делаем поля доступными
        Field[] fields = clazz.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> field.setAccessible(true));

        // Добавляем значения листа в StringBuilder
        data.forEach(x -> {
            Arrays.stream(fields).forEach(field -> {
                try {
                    Object value = field.get(x);
                    String strValue;
                    if (value == null) {
                        strValue = "";
                    }else if(value instanceof Collection || value instanceof Map) {
                        strValue ="\"" + value + "\"";
                    }else if(value.getClass().isArray()){
                        strValue ="\"" + Arrays.toString((Object[]) value) + "\"";
                    }else{
                        strValue = value.toString();
                    }

                    builder.append(strValue).append(",");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Ошибка доступа к полю", e);
                }

            });
            if (!builder.isEmpty() && builder.charAt(builder.length() - 1) == ',') {
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append("\n");
        });

        // Запись в csv-файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".csv"))) {
            writer.write(builder.toString());
        } catch (IOException ex) {
            throw new RuntimeException("Ошибка записи в файл", ex);
        }
    }

    private boolean validData(List<?> data, String fileName){
        return data == null || data.isEmpty() || fileName == null || fileName.isEmpty();
    }

}
