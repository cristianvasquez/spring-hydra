package cc.openthings.hl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import de.escalon.hypermedia.hydra.mapping.Expose;
import de.escalon.hypermedia.hydra.mapping.Term;
import de.escalon.hypermedia.hydra.mapping.Vocab;
import de.escalon.hypermedia.hydra.serialize.JacksonHydraSerializer;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JacksonHydraSerializerTest {

    private ObjectMapper mapper;

    StringWriter w = new StringWriter();


    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        // see https://github.com/json-ld/json-ld.org/issues/76
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.registerModule(new SimpleModule() {

            public void setupModule(SetupContext context) {
                super.setupModule(context);

                context.addBeanSerializerModifier(new BeanSerializerModifier() {

                    public JsonSerializer<?> modifySerializer(
                            SerializationConfig config,
                            BeanDescription beanDesc,
                            JsonSerializer<?> serializer) {

                        if (serializer instanceof BeanSerializerBase) {
                            return new JacksonHydraSerializer(
                                    (BeanSerializerBase) serializer);
                        } else {
                            return serializer;
                        }
                    }
                });
            }
        });

    }

    @Test
    public void testDefaultVocabIsRendered() throws Exception {

        class Person {
            private String name = "Dietrich Schulten";

            public String getName() {
                return name;
            }
        }

        mapper.writeValue(w, new Person());
        System.out.println(w);
        assertEquals("{\"@context\":{" +
                        "\"@vocab\":\"http://schema.org/\"" +
                        "}" +
                        ",\"@type\":\"Person\"," +
                        "\"name\":\"Dietrich Schulten\"}"
                , w.toString());
    }

    @Vocab("http://xmlns.com/foaf/0.1/")
    class Person {
        private String name = "Dietrich Schulten";

        public String getName() {
            return name;
        }
    }

    @Test
    public void testFoafVocabIsRendered() throws Exception {

        mapper.writeValue(w, new Person());
        assertEquals("{\"@context\":{" +
                        "\"@vocab\":\"http://xmlns.com/foaf/0.1/\"" +
                        "}" +
                        ",\"@type\":\"Person\"," +
                        "\"name\":\"Dietrich Schulten\"}"
                , w.toString());
    }

    @Test
    public void testNestedContextWithDifferentVocab() throws Exception {

        @Vocab("http://purl.org/dc/elements/1.1/")
        @Expose("BibliographicResource")
        class Document {
            public String title = "Moby Dick";
            public Person creator = new Person();
        }

        mapper.writeValue(w, new Document());
        assertEquals("{\"@context\":{" +
                        "\"@vocab\":\"http://purl.org/dc/elements/1.1/\"" +
                        "}" +
                        ",\"@type\":\"BibliographicResource\"" +
                        ",\"title\":\"Moby Dick\"" +
                        ",\"creator\":{" +
                        "\"@context\":{" +
                        "\"@vocab\":\"http://xmlns.com/foaf/0.1/\"}" +
                        ",\"@type\":\"Person\"" +
                        ",\"name\":\"Dietrich Schulten\"}}"
                , w.toString());
    }

    @Test
    public void testDefaultVocabWithCustomTerm() throws Exception {

        class Person {
            public String birthDate;
            public String firstName;

            // override field name by schema.org property
            @Expose("familyName")
            public String lastName;

            // override getter by schema.org property
            @Expose("givenName")
            public String getFirstName() {
                return firstName;
            }

            public Person(String birthDate, String firstName, String lastName) {
                this.birthDate = birthDate;
                this.lastName = lastName;
                this.firstName = firstName;
            }
        }


        mapper.writeValue(w, new Person("1964-08-08", "Dietrich", "Schulten"));
        assertEquals("{\"@context\":{" +
                        "\"@vocab\":\"http://schema.org/\"," +
                        "\"lastName\":\"familyName\"," +
                        "\"firstName\":\"givenName\"" +
                        "}," +
                        "\"@type\":\"Person\"," +
                        "\"birthDate\":\"1964-08-08\"," +
                        "\"firstName\":\"Dietrich\"," +
                        "\"lastName\":\"Schulten\"}"
                , w.toString());

    }

    class Movie {
        public String name = "Pirates of the Caribbean";
        public String description = "Jack Sparrow and Barbossa embark on a quest.";
        public String model = "http://www.imdb.com/title/tt0325980/";
        public List<Offer> offers = Arrays.asList(new Offer());
    }

    @Term(define = "gr", as = "http://purl.org/goodrelations/v1#")
    class Offer {
        public BusinessFunction businessFunction = BusinessFunction.RENT;
        public UnitPriceSpecification priceSpecification = new UnitPriceSpecification();
        private DeliveryMethod availableDeliveryMethod = DeliveryMethod.DOWNLOAD;
        public QuantitativeValue eligibleDuration = new QuantitativeValue();

        public DeliveryMethod getAvailableDeliveryMethod() {
            return availableDeliveryMethod;
        }

        public void setAvailableDeliveryMethod(DeliveryMethod availableDeliveryMethod) {
            this.availableDeliveryMethod = availableDeliveryMethod;
        }
    }

    enum DeliveryMethod {
        @Expose("gr:DeliveryModeDirectDownload")
        DOWNLOAD
    }

    enum BusinessFunction {
        @Expose("gr:LeaseOut")
        RENT,
        @Expose("gr:Sell")
        FOR_SALE,
        @Expose("gr:Buy")
        BUY
    }

    @Term(define = "gr", as="http://purl.org/goodrelations/v1#")
    class UnitPriceSpecification {
        public BigDecimal price = BigDecimal.valueOf(3.99);
        public String priceCurrency = "USD";
        public String datetime = "2012-12-31T23:59:59Z";
    }

    class QuantitativeValue {
        public String value = "30";
        public String unitCode = "DAY";
    }

}