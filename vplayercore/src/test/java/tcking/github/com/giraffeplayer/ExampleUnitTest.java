package tcking.github.com.giraffeplayer;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        PlayerModel model = new PlayerModel("hello.mp4",null);
        assertEquals("Hello",PlayerModel.MediaType.VIDEO,model.getType());
    }
}