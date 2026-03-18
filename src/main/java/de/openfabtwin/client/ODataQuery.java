package de.openfabtwin.client;

public class ODataQuery {
    String $filter;
    String $orderby;

    public ODataQuery filter(String filter) { this.$filter = filter;  return this; }
    public ODataQuery orderby(String orderby) { this.$orderby = orderby; return this; }
}
