package org.bobba.tools.statest.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import org.bobba.tools.statest.utils.StatestCommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notNull;

public final class RestAssuredCodeGenerator {

    private RestAssuredCodeGenerator() {
    }

    public static Matcher<?> assertionGeneratingMatcher() {
        final String codePointer = StatestCommonUtils.createCodePointer(-1);
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object item) {
                notNull(item);
                System.out.println("*** Generating assertions for " + codePointer);
                System.out.println(generateAssertionsForJson((String) item));
                System.out.println("*** " + codePointer);
                throw new RuntimeException("Stopped by assertion generator");
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    public static String generateAssertionsForJson(String json) {
        final JsonNode jsonNode = read(json);
        return generateAssertions(jsonNode);
    }

    private static String generateAssertions(JsonNode jsonNode) {
        return AssertionBuilder.generateAssertions("", jsonNode);
    }

    private static JsonNode read(String json) {
        try {
            return new ObjectMapper().readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class AssertionBuilder {

        private final List<String> lines = new ArrayList<>();

        private final NodeHandlerFactory nodeHandlerFactory = createNodeHandlerFactory();

        private static NodeHandlerFactory createNodeHandlerFactory() {
            final ImmutableMap<Class<? extends JsonNode>, NodeHandler<? extends JsonNode>>
                    nodeHandlers = ImmutableMap.<Class<? extends JsonNode>, NodeHandler<? extends JsonNode>>builder()
                    .put(ObjectNode.class, new ObjectNodeHandler())
                    .put(TextNode.class, new TextNodeHandler())
                    .put(NullNode.class, new NullNodeHandler())
                    .put(BooleanNode.class, new BooleanNodeHandler())
                    .put(LongNode.class, new LongNodeHandler())
                    .put(IntNode.class, new IntNodeHandler())
                    .put(ArrayNode.class, new ArrayNodeHandler())
                    .build();

            return new NodeHandlerFactory() {
                @Override
                public <T extends JsonNode> NodeHandler<T> getHandler(
                        Class<T> nodeClass) {
                    //noinspection unchecked
                    final NodeHandler<T> result = (NodeHandler<T>) nodeHandlers.get(nodeClass);
                    if (result == null) {
                        throw new RuntimeException("Unexpected node type: " + nodeClass);
                    }
                    return result;
                }
            };
        }

        public String build() {
            return Joiner.on('\n').join(lines);
        }

        public static String generateAssertions(String context, JsonNode rootNode) {
            final AssertionBuilder assertionBuilder = new AssertionBuilder();
            assertionBuilder.appendAssertions(context, rootNode);
            return assertionBuilder.build();
        }

        private void appendAssertions(String context, JsonNode jsonNode) {
            final NodeHandler nodeHandler = getNodeHandler(jsonNode.getClass());

            @SuppressWarnings("unchecked")
            final String result = nodeHandler.generateAssertions(context, jsonNode, nodeHandlerFactory);

            lines.add(result);
        }

        private NodeHandler getNodeHandler(Class<? extends JsonNode> aClass) {
            return nodeHandlerFactory.getHandler(aClass);
        }

        private static String createBodyText(String field, String assertion) {
            return ".body(\"" + field + "\", " + assertion + ")";
        }

        private static String prefixed(String prefix, String field) {
            return StringUtils.isEmpty(prefix) ? field : prefix + "." + field;
        }

        private interface NodeHandler<T extends JsonNode> {
            String generateAssertions(String context, T node,
                                      NodeHandlerFactory nodeHandlerFactory);
        }

        private interface NodeHandlerFactory {
            <T extends JsonNode> NodeHandler<T> getHandler(Class<T> nodeClass);
        }

        private static class ObjectNodeHandler implements NodeHandler<ObjectNode> {

            @Override
            public String generateAssertions(String context, ObjectNode node, NodeHandlerFactory nodeHandlerFactory) {
                final Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
                final List<String> result = new ArrayList<>();
                if (StringUtils.isNotEmpty(context)) {
                    result.add(createBodyText(context, "notNullValue()"));
                }
                while (iterator.hasNext()) {
                    final Map.Entry<String, JsonNode> entry = iterator.next();
                    final JsonNode childNode = entry.getValue();
                    try {
                        result.add(generateAssertions(context, entry.getKey(), childNode, nodeHandlerFactory));
                    } catch (RuntimeException e) {
                        throw new RuntimeException(
                                "Error when handling node: " + entry.getKey() + ", value:" + childNode, e);
                    }
                }
                return Joiner.on('\n').join(result);
            }

            @SuppressWarnings("unchecked")
            private String generateAssertions(String prefix, String name, JsonNode node,
                                              NodeHandlerFactory nodeHandlerFactory) {
                notNull(node);
                final NodeHandler nodeHandler = nodeHandlerFactory.getHandler(node.getClass());
                return nodeHandler.generateAssertions(prefixed(prefix, name), node, nodeHandlerFactory);
            }

        }

        private static class ArrayNodeHandler implements NodeHandler<ArrayNode> {
            @Override
            public String generateAssertions(String context, ArrayNode node, NodeHandlerFactory nodeHandlerFactory) {
                final List<String> result = new ArrayList<>();
                final String simpleContext = StringUtils.defaultIfEmpty(context, "$");
                result.add(createBodyText(simpleContext, "notNullValue()"));
                result.add(createBodyText(simpleContext + ".size()", "is(" + node.size() + ")"));
                final Iterator<JsonNode> iterator = node.elements();
                int i = 0;
                while (iterator.hasNext()) {
                    final JsonNode childNode = iterator.next();
                    result.add(createAssertions(context, i, childNode));
                    i++;
                }
                return Joiner.on('\n').join(result);
            }

            private String createAssertions(String context, int index, JsonNode childNode) {
                notNull(childNode);

                return AssertionBuilder.generateAssertions(context + "[" + index + "]", childNode);
            }
        }

        private static abstract class AbstractValueNodeHandler<T extends JsonNode> implements NodeHandler<T> {
            @Override
            public String generateAssertions(String context, T node,
                                             NodeHandlerFactory nodeHandlerFactory) {
                return createBodyText(context, generateValueAssertion(node));
            }

            protected abstract String generateValueAssertion(T node);
        }

        private static final class TextNodeHandler extends AbstractValueNodeHandler<TextNode> {
            @Override
            protected String generateValueAssertion(TextNode node) {
                return "is(\"" + node.textValue() + "\")";
            }
        }

        private static final class NullNodeHandler extends AbstractValueNodeHandler<NullNode> {
            @Override
            protected String generateValueAssertion(NullNode node) {
                return "nullValue()";
            }
        }

        private static final class BooleanNodeHandler extends AbstractValueNodeHandler<BooleanNode> {
            @Override
            protected String generateValueAssertion(BooleanNode node) {
                return "is(" + node.asBoolean() + ")";
            }
        }

        private static final class LongNodeHandler extends AbstractValueNodeHandler<LongNode> {
            @Override
            protected String generateValueAssertion(LongNode node) {
                return "is(" + node.longValue() + "L)";
            }
        }

        private static final class IntNodeHandler extends AbstractValueNodeHandler<IntNode> {
            @Override
            protected String generateValueAssertion(IntNode node) {
                return "is(" + node.intValue() + ")";
            }
        }
    }

}
