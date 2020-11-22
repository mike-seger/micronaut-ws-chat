package micronaut.ws.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public interface JsonAware<T> {
	ObjectMapper _objectMapper = new ObjectMapper()
		.registerModule(new JavaTimeModule())
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		;
	default String toJson() {
		try {
			return _objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Cannot serialize");
		}
	}

	default T fromJson(String json) {
		try {
			ObjectReader objectReader = _objectMapper.readerForUpdating(this);
			objectReader.readValue(json);
			//noinspection unchecked
			return (T)this;
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Cannot serialize");
		}
	}
}
