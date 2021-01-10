package com.zackmurry.cardtown;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import static com.zackmurry.cardtown.util.HtmlUtils.sanitizeHtml;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class HtmlUtilsTest {

    @DisplayName("Test removal of bad html tags")
    @Test
    public void testBadHtmlTagRemoval() {
        assertEquals("", sanitizeHtml("<script>"));
        assertEquals("", sanitizeHtml("<img src=x onerror=javascript:alert('xss') />"));
    }

    @DisplayName("Test sanitization for real-world XSS attacks")
    @Test
    public void testRealWorldXSS() {
        assertEquals("", sanitizeHtml("<IMG SRC=\"javascript:alert('XSS');\" />"));
        assertEquals("", sanitizeHtml("<IMG SRC=javascript:alert('XSS') />"));
        assertEquals("", sanitizeHtml("<IMG SRC=JaVaScRiPt:alert('XSS') />"));
        assertEquals("", sanitizeHtml("<IMG SRC=javascript:alert(&quot;XSS&quot;)>"));

        assertFalse(sanitizeHtml("\\<a onmouseover=\"alert(document.cookie)\"\\>xxs link\\</a\\>").contains("onmouseover"));
    }

    @DisplayName("Test sanitization for real-world html")
    @Test
    public void testRealWorldHtml() {
        String value = "<div><p style=\"background-color: rgb(255, 255, 0);\">test</p></div>";
        assertEquals("<div>\n <p style=\"background-color: rgb(255, 255, 0);\">test</p>\n</div>", sanitizeHtml(value));
        value = "<p>this is a <b>test</b></p>";
        assertEquals(value, sanitizeHtml(value));
    }

    @DisplayName("Test basic h_, p, b, and i tag preservation")
    @Test
    public void testBasicTags() {
        String value = "<h1>test</h1>";
        assertEquals(value, sanitizeHtml(value));
        value = "<h2>testsa dadsa dsad </h2>";
        assertEquals(value, sanitizeHtml(value));
        value = "<h3>A test for h3</h3>";
        assertEquals(value, sanitizeHtml(value));
        value = "<h4>Wait it! Another test\\</h4>";
        assertEquals(value, sanitizeHtml(value));
        value = "<h5>Yooo! Another test but for h5</h5>";
        assertEquals(value, sanitizeHtml(value));
        value = "<h6>A simple preset test</h6>";
        assertEquals(value, sanitizeHtml(value));

        value = "<p><b>A simple <i>combined</i> test</b></p>";
        assertEquals(value, sanitizeHtml(value));
    }

}
