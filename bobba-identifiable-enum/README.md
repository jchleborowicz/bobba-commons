# bobba identifiable enum

## Intro

Tiny utility class for enums with identifiers.

The concept is that java enum will have an identifier of arbitrary type.

This makes enum identifiers independent of enum ordinal position or enum names.

Sample usage of enum identifiers:
* store enum id in database
* send enum id via JSON

## Code example

Enum with string identifiers would look like follows:
```java
import java.util.Map;

//Implements IdentifiableEnum<String> which sets identifier's type to "String"
public enum Status implements IdentifiableEnum<String> {

    //each enum has a id specified as constructor parameter
    CREATED("CR"),
    ACCEPTED("AC"),
    CLOSED("CL");

    //static field wchich maps ids to enum values, needed below to obtain enum by id.
    private static final Map<String, Status> valuesById = IdentifiableEnumHelper.create(values());

    //id field
    private final String id;
    
    Status(String id) {
        this.id = id;
    }

    //obligatory interface method implementation
    @Override
    public String getId() {
        return this.id;
    }

    //getting enum values by id
    public static Status byId(String id) {
        return valuesById.get(id);
    }

}

```

## Hibernate

Library contains hibernate type mapper for identifiable enums - GenericEnumUserType.

hibernate dependency scope is provided, so you can use this library without necessity to include 
hibernate jars in your classpath.
