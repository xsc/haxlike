package haxlike;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MainTest {
    
    @Test
    void resolve_shouldResolveTheFullTree() {
        Object node = null;
        Object expected = null;

        assertThat(Main.resolve(node)).isEqualTo(expected);
    }
}
