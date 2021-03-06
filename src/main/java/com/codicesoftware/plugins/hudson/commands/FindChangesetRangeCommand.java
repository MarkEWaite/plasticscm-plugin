package com.codicesoftware.plugins.hudson.commands;

import com.codicesoftware.plugins.hudson.model.ChangeSet;
import com.codicesoftware.plugins.hudson.util.DateUtil;
import com.codicesoftware.plugins.hudson.util.MaskedArgumentListBuilder;
import hudson.util.Digester2;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class FindChangesetRangeCommand implements ParseableCommand<List<ChangeSet>>, Command {

    private final int csetIdFrom;
    private final int csetIdTo;
    private final String branch;
    private final String repository;

    public FindChangesetRangeCommand(int csetIdFrom, int csetIdTo, String branch, String repository) {
        this.csetIdFrom = csetIdFrom;
        this.csetIdTo = csetIdTo;
        this.branch = branch;
        this.repository = repository;
    }

    public MaskedArgumentListBuilder getArguments() {
        MaskedArgumentListBuilder arguments = new MaskedArgumentListBuilder();

        arguments.add("find");
        arguments.add("changeset");
        arguments.add("where");
        arguments.add("branch='" + branch + "'");
        arguments.add("and");
        arguments.add("changesetid");
        arguments.add("between");
        arguments.add(csetIdFrom);
        arguments.add("and");
        arguments.add(csetIdTo);
        arguments.add("on");
        arguments.add("repository");
        arguments.add("'" + repository + "'");

        arguments.add("--xml");
        arguments.add("--dateformat=" + DateUtil.ISO_DATE_TIME_OFFSET_CSHARP_FORMAT);

        return arguments;
    }

    public List<ChangeSet> parse(Reader reader) throws IOException, ParseException {
        ArrayList<ChangeSet> csetList = new ArrayList<>();

        Digester digester = new Digester2();
        digester.push(csetList);

        digester.addObjectCreate("*/CHANGESET", ChangeSet.class);
        digester.addBeanPropertySetter("*/CHANGESET/CHANGESETID", "version");
        digester.addBeanPropertySetter("*/CHANGESET/COMMENT", "comment");
        digester.addBeanPropertySetter("*/CHANGESET/DATE", "xmlDate");
        digester.addBeanPropertySetter("*/CHANGESET/BRANCH", "branch");
        digester.addBeanPropertySetter("*/CHANGESET/OWNER", "user");
        digester.addBeanPropertySetter("*/CHANGESET/REPNAME", "repoName");
        digester.addBeanPropertySetter("*/CHANGESET/REPSERVER", "repoServer");
        digester.addBeanPropertySetter("*/CHANGESET/GUID", "guid");
        digester.addSetNext("*/CHANGESET", "add");

        try {
            digester.parse(reader);
        } catch (SAXException e) {
            throw new ParseException("Parse error: " + e.getMessage(), 0);
        }

        return csetList;
    }
}
