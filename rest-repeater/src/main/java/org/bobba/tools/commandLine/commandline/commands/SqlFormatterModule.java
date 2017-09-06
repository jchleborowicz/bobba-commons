package org.bobba.tools.commandLine.commandline.commands;

import org.bobba.tools.commandLine.ClipboardService;
import org.bobba.tools.commandLine.commandline.Command;
import org.bobba.tools.commandLine.commandline.CommandLineBusinessException;
import org.bobba.tools.commandLine.commandline.CommandLineModule;
import org.bobba.tools.commandLine.commandline.CommandLineOutput;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandLineModule(name = "sql")
public class SqlFormatterModule {

    @Autowired
    private ClipboardService clipboardService;

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
        String result = "INSERT INTO " + insertStatement.getTableName() + " (\n";
        final InsertField lastField = insertStatement.getFields().get(insertStatement.getFields().size() - 1);
        for (InsertField insertField : insertStatement.getFields()) {
            result += "    " + insertField.getName();
            if (insertField != lastField) {
                result += ",";
            }
            result += "\n";
        }
        result += ") VALUES (\n";
        for (InsertField insertField : insertStatement.getFields()) {
            result += "    " + insertField.getValue();
            if (insertField != lastField) {
                result += ",";
            }
            result += " --" + insertField.getName() + "\n";
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
            throw new CommandLineBusinessException("ERROR: Wrong inser fromat:\n" + insertSql);
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
        private List<InsertField> fields = new ArrayList<InsertField>();

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
