package com.demo.folder.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
public class ErrorStat {

    private String errorName;
    @Setter
    private Integer count;

    public ErrorStat() {
    }

    @JsonCreator
    public ErrorStat(
            @JsonProperty("errorName") String errorName,
            @JsonProperty("count") Integer count
    ) {
        this.errorName = errorName;
        this.count = count;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorStat errorStat = (ErrorStat) o;
        return Objects.equals(errorName, errorStat.errorName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorName);
    }
}
