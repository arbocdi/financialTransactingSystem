package kg.arbocdi.fts.core.cfg;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kg.arbocdi.fts.core.msg.Message;

@Converter(autoApply = false)
public class MessageJsonConverter implements AttributeConverter<Message, String> {

    @Override
    public String convertToDatabaseColumn(Message attribute) {
        if (attribute == null) return null;
        try {
            return JsonMapperHolder.get().writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON serialize failed", e);
        }
    }

    @Override
    public Message convertToEntityAttribute(String str) {
        if (str == null) return null;
        try {
            return JsonMapperHolder.get().readValue(str, Message.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON deserialize failed", e);
        }
    }
}
