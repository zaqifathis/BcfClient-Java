package de.openfabtwin.domain;

import lombok.Getter;

@Getter
public class TopicFilter {
    private String filter;
    private String orderBy;
    private Integer top;
    private Integer skip;

    public TopicFilter filter(String filter) {
        this.filter = filter;
        return this;
    }

    public TopicFilter orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public TopicFilter top(int top) {
        this.top = top;
        return this;
    }

    public TopicFilter skip(int skip) {
        this.skip = skip;
        return this;
    }

    public String toQueryString() {
        StringBuilder sb = new StringBuilder();

        if (filter != null)  append(sb, "$filter",  filter);
        if (orderBy != null) append(sb, "$orderby", orderBy);
        if (top != null)     append(sb, "$top",     top.toString());
        if (skip != null)    append(sb, "$skip",    skip.toString());

        return sb.toString();
    }

    private void append(StringBuilder sb, String key, String value) {
        sb.append(sb.length() == 0 ? "?" : "&");
        sb.append(key).append("=").append(value);
    }
}
