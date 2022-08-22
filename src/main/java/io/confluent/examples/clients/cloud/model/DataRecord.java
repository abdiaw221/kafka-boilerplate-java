package io.confluent.examples.clients.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataRecord {

    private Long id;
    private Long count;

    public DataRecord(Long id, Long count) {
        this.id = id;
        this.count = count;

    }

    public Long getId() {
        return id;
    }

    public Long getCount() {
        return count;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }

}
