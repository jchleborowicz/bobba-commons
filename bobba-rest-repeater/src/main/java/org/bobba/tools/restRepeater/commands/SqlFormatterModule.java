package org.bobba.tools.restRepeater.commands;

import org.apache.commons.lang3.StringUtils;
import org.bobba.tools.restRepeater.ClipboardService;
import org.bobba.tools.commandLine.Command;
import org.bobba.tools.commandLine.CommandLineBusinessException;
import org.bobba.tools.commandLine.CommandLineModule;
import org.bobba.tools.commandLine.CommandLineOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandLineModule(name = "sql")
public class SqlFormatterModule {

    private final ClipboardService clipboardService;

    public SqlFormatterModule(ClipboardService clipboardService) {
        this.clipboardService = clipboardService;
    }

    @Command(names = "fi", description = "Formats insert sql. Reads input from clipboard.")
    public void execute(CommandLineOutput output) {
        final String content = clipboardService.getClipboardContent();
        final String transform = transform(content);
        clipboardService.setClipboardContent(transform);
        output.println(transform);
    }

    private String transform(String insertSql) {
        final InsertStatement insertStatement = parseInsertStatement(insertSql);

        return createInsertSql(insertStatement);
    }

    private String createInsertSql(InsertStatement insertStatement) {
        final StringBuilder result = new StringBuilder("INSERT INTO " + insertStatement.getTableName() + " (\n");
        final InsertField lastField = insertStatement.getFields().get(insertStatement.getFields().size() - 1);
        for (InsertField insertField : insertStatement.getFields()) {
            result.append("    ").append(insertField.getName());
            if (insertField != lastField) {
                result.append(",");
            }
            result.append("\n");
        }
        result.append(") VALUES (\n");
        for (InsertField insertField : insertStatement.getFields()) {
            result.append("    ").append(insertField.getValue());
            if (insertField != lastField) {
                result.append(",");
            }
            result.append(" --").append(insertField.getName()).append("\n");
        }

        return result + ");";
    }

    private InsertStatement parseInsertStatement(String insertSql) {
        if (StringUtils.isBlank(insertSql)) {
            throw new CommandLineBusinessException("ERROR: Clipboard is empty");
        }
        final Pattern pattern = Pattern.compile("INSERT INTO ([\\.\\w]+)\\S*\\((.*)\\) VALUES \\((.*)\\);");
        final Matcher matcher = pattern.matcher(insertSql);

        if (!matcher.find()) {
            throw new CommandLineBusinessException("ERROR: Wrong insert format:\n" + insertSql);
        }

        final InsertStatement insertStatement = new InsertStatement();

        insertStatement.setTableName(matcher.group(1));
        final String fields = matcher.group(2);
        final String values = matcher.group(3);
        final String[] fieldsArray = fields.split(",");
        final String[] valuesArray = values.split(",");
        if (fieldsArray.length != valuesArray.length) {
            throw new RuntimeException("field count is not equal to value count");
        }
        for (int i = 0; i < fieldsArray.length; i++) {
            String field = fieldsArray[i];
            String value = valuesArray[i];
            insertStatement.addField(field.trim(), value.trim());
        }
        return insertStatement;
    }

    private static class InsertStatement {
        private String tableName;
        private List<InsertField> fields = new ArrayList<>();

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public void addField(String name, String value) {
            this.fields.add(new InsertField(name, value));
        }

        public String getTableName() {
            return tableName;
        }

        public List<InsertField> getFields() {
            return fields;
        }
    }

    private static class InsertField {
        private final String name;
        private final String value;

        public InsertField(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

}
