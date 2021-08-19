package cloud.ptl.povserver.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.File;

@Converter
public class FileToStringConverter implements AttributeConverter<File, String> {
    @Override
    public String convertToDatabaseColumn(File file) {
        if (file == null) return "";
        else {
            return file.getAbsolutePath();
        }
    }

    @Override
    public File convertToEntityAttribute(String s) {
        if (s.isBlank()) return null;
        else {
            return new File(s);
        }
    }
}
