package flowdesigner.jdbc.download;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class PomParserTest {

    @Test
    void getDependencies() {
        Collection<Dependency> dependencies = PomParser.getDependencies("C:\\code\\git\\dynamicJDBC\\pom.xml", null);
    System.out.println(dependencies);
    }
}