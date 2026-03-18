package de.openfabtwin.client;

public class FullODataQuery extends ODataQuery {
    String $top;
    String $skip;

    @Override
    public FullODataQuery filter(String filter)   { this.$filter  = filter;  return this; }
    @Override
    public FullODataQuery orderby(String orderby) { this.$orderby = orderby; return this; }
    public FullODataQuery top(int top)            { this.$top     = String.valueOf(top);  return this; }
    public FullODataQuery skip(int skip)          { this.$skip    = String.valueOf(skip); return this; }
}
